package com.dsys.appfood.service;

import com.dsys.appfood.domain.enums.FormaPagamento;
import com.dsys.appfood.domain.enums.StatusPedido;
import com.dsys.appfood.domain.model.Caixa;
import com.dsys.appfood.domain.model.Pagamento;
import com.dsys.appfood.domain.model.Pedido;
import com.dsys.appfood.domain.model.Usuario;
import com.dsys.appfood.dto.PagamentoComTrocoResponse;
import com.dsys.appfood.exception.NegocioException;
import com.dsys.appfood.exception.PagamentoNaoEncontradoException;
import com.dsys.appfood.repository.PagamentoRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Serviço responsável pelo processamento de pagamentos.
 * 
 * Princípio da Responsabilidade Única (SOLID): - Este serviço só processa
 * PAGAMENTOS - Delega para CaixaService o registro de movimentação - Delega
 * para PedidoService a busca do pedido
 */
@Service
public class PagamentoService {

	private final UsuarioService usuarioService;
	private final CaixaService caixaService;
	private final PedidoService pedidoService;
	private final PagamentoRepository pagamentoRepository;

	public PagamentoService(PagamentoRepository pagamentoRepository, PedidoService pedidoService,
			CaixaService caixaService, UsuarioService usuarioService) {

		this.pagamentoRepository = pagamentoRepository;
		this.pedidoService = pedidoService;
		this.caixaService = caixaService;
		this.usuarioService = usuarioService;
	}

	// ============================================================
	// REGISTRAR PAGAMENTO (método principal)
	// ============================================================
	@Transactional
	public Pagamento registrarPagamento(Integer pedidoId, Integer caixaId, FormaPagamento forma, BigDecimal valor,
			Integer operadorId) {

		// 1. VALIDAÇÕES SEM BANCO

		// Verifica se o valor é válido
		if (valor == null || valor.signum() <= 0) {
			throw new IllegalArgumentException("O valor do pagamento deve ser maior que zero");
		}

		// Verifica se a forma de pagamento é válida
		if (forma == null) {
			throw new IllegalArgumentException("A forma de pagamento deve ser informada");
		}

		// 2. BUSCAR ENTIDADES RELACIONADAS

		// Busca o pedido (precisa estar aberto)
		Pedido pedido = pedidoService.buscarPorId(pedidoId);

		// Busca o Caixa
		Caixa caixa = caixaService.buscaCaixaAberto(caixaId);

		// Buscar o operador (para registro de quem fez)
		Usuario operador = usuarioService.buscaPorId(operadorId);

		// 3. REGRAS DE NEGÓCIO

		// REGRA 1: Não pode pagar pedido já finalizado/cancelado
		if (pedido.getStatus() == StatusPedido.FINALIZADO || pedido.getStatus() == StatusPedido.CANCELADO) {
			throw new NegocioException("Não é possivel adicionar pagamento a um pedido " + pedido.getStatus());
		}

		// REGRA 2: Não pode pagar mais que o necessário
		BigDecimal valorRestante = pedido.getValorRestante();

		if (valor.compareTo(valorRestante) > 0) {
			throw new NegocioException(String.format(
					"Valor do pagamento (R$ %.2f) excede o valor restante do pedido (R$ %.2f)", valor, valorRestante));
		}

		// 4. CRIAR E PERSISTIR O PAGAMENTO

		// construindo o objeto passo a passo
		Pagamento pagamento = new Pagamento();
		pagamento.associarPedido(pedido);
		pagamento.setCaixa(caixa);
		pagamento.setFormaPagamento(forma);
		pagamento.setValor(valor);
		pagamento.setOperador(operador);

		// O método registrarPagamento da classe Pedido:
		// - Adiciona este pagamento à lista de pagamentos do pedido
		// - Verifica se o pedido ficou quitado e atualiza o status
		// - Mantém a consistência do relacionamento bidirecional
		pedido.registrarPagamento(pagamento);

		// Salva o pagamento
		Pagamento pagamentoSalvo = pagamentoRepository.save(pagamento);

		// 5. SE FOR DINHEIRO, REGISTRA ENTRADA NO CAIXA

		// PagamentoService delega para CaixaService a responsabilidade de registrar a
		// movimentação financeira
		if (forma == FormaPagamento.DINHEIRO)
			// Registra a entrada física no caixa
			caixaService.registrarVenda(caixaId, pedidoId, valor);

		// 6. VERIFICAR SE PEDIDO FOI QUITADO

		// Se o pedido ficou totalmente salvo atualiza o status
		if (pedido.isPago() && pedido.getStatus() != StatusPedido.CANCELADO) {
			pedidoService.mudarStatus(pedidoId, StatusPedido.FINALIZADO, operadorId);
		}

		return pagamentoSalvo;
	}

	// ============================================================
	// PAGAMENTO MÚLTIPLO (VÁRIAS FORMAS DE UMA VEZ)
	// ============================================================

	/**
	 * REGISTRA MÚLTIPLOS PAGAMENTOS DE UMA VEZ
	 * 
	 * CONCEITO: Composição de operações
	 * 
	 * Este método recebe uma lista de valores e formas de pagamento e processa cada
	 * um individualmente, chamando o método registrarPagamento. - Transação
	 * atômica: ou todos os pagamentos são registrados, ou nenhum
	 * 
	 * @param pedidoId   - ID do pedido
	 * @param caixaId    - ID do caixa
	 * @param pagamentos - Lista de DTOs com forma e valor
	 * @param operadorId - Quem está operando
	 */
	@Transactional
	public List<Pagamento> registrarPagamentoMultiplo(Integer pedidoId, Integer caixaId, List<Pagamento> pagamentos,
			Integer operadorId) {

		// Validação: Precisa ter pelo menos um pagamento
		if (pagamentos == null || pagamentos.isEmpty()) {
			throw new IllegalArgumentException("É necessário informar pelo menos um pagamento");
		}

		return pagamentos.stream()
				.map(p -> registrarPagamento(pedidoId, caixaId, p.getFormaPagamento(), p.getValor(), operadorId))
				.collect(Collectors.toList());
	}

	// ============================================================
	// CÁLCULO DE TROCO
	// ============================================================

	/**
	 * CALCULA O TROCO PARA PAGAMENTO EM DINHEIRO
	 * 
	 * O operador informa quanto o cliente deu em dinheiro, e o sistema calcula o
	 * troco
	 * 
	 * @param pedidoId      - ID do pedido
	 * @param valorRecebido - Quanto o cliente entregou
	 * @return BigDecimal - Valor do troco (pode ser zero)
	 * 
	 */
	@Transactional(readOnly = true)
	public BigDecimal calcularTroco(Integer pedidoId, BigDecimal valorRecebido) {

		// Validação
		if (valorRecebido == null || valorRecebido.signum() < 0) {

			throw new IllegalArgumentException("Valor recebido deve ser maior ou igual a zero.");
		}

		Pedido pedido = pedidoService.buscarPorId(pedidoId);

		// Quanto falta pagar (considerando pagamentos já efetuados)
		BigDecimal valorRestante = pedido.getValorRestante();

		// Se valor recebido é menor que o restante, não é troco, é pagamento parcial
		if (valorRecebido.compareTo(valorRecebido) < 0) {
			return BigDecimal.ZERO; // ainda não tem troco
		}

		// - BigDecimal.setScale(): Calcula o troco com 2 casas decimais
		// - RoundingMode.HALF_UP: arredondamento padrão (>= 0.5 arredonda para cima)
		return valorRecebido.subtract(valorRestante).setScale(2, RoundingMode.HALF_UP);
	}

	// ============================================================
	// PROCESSAR PAGAMENTO COM TROCO
	// ============================================================
	
	/**
	 * Processar pagamento em dinheiro com cálculo extato de troco   
	 * 
	 * @return PagamentoResponse - DTO com pagamento e valor do troco
	 */
	@Transactional
	public PagamentoComTrocoResponse processarPagamentoDinheiro(
			Integer pedidoId, Integer caixaId, BigDecimal valorRecebido, Integer operadorId) {
		
		Pedido pedido = pedidoService.buscarPorId(pedidoId);
		BigDecimal valorRestante = pedido.getValorRestante();
		
		//Calcula o troco primeiro
		BigDecimal troco = calcularTroco(pedidoId, valorRecebido);
		
		// Se o valor recebido é MAIOR que o necessário, registra apenas o valor
        // necessário para quitar o pedido (o resto é troco e NÃO entra no caixa)
		BigDecimal valorARegistrar;
		if(valorRecebido.compareTo(valorRestante) > 0) {
			valorARegistrar = valorRestante; // Só registra o que realmente é devido
		}else {
			valorARegistrar = valorRecebido; // Pagamento parcial
		}
		
		//Registra o pagamento
		Pagamento pagamento = registrarPagamento(pedidoId, caixaId, FormaPagamento.DINHEIRO, valorARegistrar, operadorId);
		
		// Monta reposta
		PagamentoComTrocoResponse response = new PagamentoComTrocoResponse();
		response.setPagamento(pagamento);
		response.setTroco(troco);
		response.setValorRecebido(valorRecebido);
		
		return response;
	}
	
	// ============================================================
    // ESTORNAR PAGAMENTO (caso de erro)
    // ============================================================

	@Transactional
    public void estornarPagamento(Integer pagamentoId, String motivo, Integer gerenteId) {
        
        Pagamento pagamento = buscarPorId(pagamentoId);
        Pedido pedido = pagamento.getPedido();
        
        // REGRA: Não pode estornar pagamento de pedido finalizado
        if (pedido.getStatus() == StatusPedido.FINALIZADO) {
            throw new NegocioException(
                "Não é possível estornar pagamento de um pedido já finalizado."
            );
        }
        
        // Verifica se o gerente tem autorização
        Usuario gerente = usuarioService.buscaPorId(gerenteId);
        if (!gerente.isGerente()) {
            throw new NegocioException(
                "Apenas gerentes podem estornar pagamentos."
            );
        }
        
        // Se for DINHEIRO, precisa registrar a SAÍDA no caixa
        if (pagamento.getFormaPagamento() == FormaPagamento.DINHEIRO) {
            caixaService.registrarEstorno(
                pagamento.getCaixa().getId(),
                pagamento.getValor(),
                gerente,
                "Estorno de pagamento #" + pagamentoId + ": " + motivo
            );
        }
        
        // Remove o pagamento da lista do pedido
        pedido.getPagamentos().remove(pagamento);
        
        // Exclui o pagamento
        pagamentoRepository.delete(pagamento);
        
        // Recalcula status de pagamento do pedido
        pedido.setPedidoPago(pedido.isPago());
    }
	
	// ============================================================
    // CONSULTAS
    // ============================================================
	
	/**
     * LISTAR PAGAMENTOS DE UM PEDIDO
     * 
     * @Transactional(readOnly = true) - Otimização
     */
    @Transactional(readOnly = true)
    public List<Pagamento> listarPagamentosDoPedido(Integer pedidoId) {
        // Verifica se o pedido existe (lança exceção se não)
        pedidoService.buscarPorId(pedidoId);
        
        return pagamentoRepository.findByPedidoIdOrderByDataHoraDesc(pedidoId);
    }
    
    /**
     * CALCULAR TOTAL PAGO DE UM PEDIDO
     * 
     * Método de conveniência - encapsula a lógica de soma
     */
    @Transactional(readOnly = true)
    public BigDecimal calcularTotalPago(Integer pedidoId) {
        return listarPagamentosDoPedido(pedidoId).stream()
            .map(Pagamento::getValor)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    /**
     * VERIFICAR SE PEDIDO ESTÁ TOTALMENTE PAGO
     */
    @Transactional(readOnly = true)
    public boolean isPedidoQuitado(Integer pedidoId) {
        Pedido pedido = pedidoService.buscarPorId(pedidoId);
        return pedido.isPago();
    }
	
	/**
     * BUSCAR PAGAMENTO POR ID
     */
	public Pagamento buscarPorId(Integer id) {
		return pagamentoRepository.findById(id).orElseThrow(() -> new PagamentoNaoEncontradoException(id));
	}
}
