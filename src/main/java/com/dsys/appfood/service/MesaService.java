package com.dsys.appfood.service;

import com.dsys.appfood.domain.enums.StatusPedido;
import com.dsys.appfood.domain.enums.TipoPedido;
import com.dsys.appfood.domain.model.Mesa;
import com.dsys.appfood.domain.model.Pedido;
import com.dsys.appfood.dto.request.MesaCadastroRequest;
import com.dsys.appfood.dto.request.MesaStatusRequest;
import com.dsys.appfood.dto.response.MesaResponse;
import com.dsys.appfood.exception.MesaJaCadastradaException;
import com.dsys.appfood.exception.MesaNaoEncontradaException;
import com.dsys.appfood.exception.NegocioException;
import com.dsys.appfood.repository.MesaRepository;

import com.dsys.appfood.repository.PedidoRepository;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Classe responsável por gerenciar o ciclo de vida das mesas do
 * estabelecimento.
 * 
 * Responsabilidade ÚNICA: gerenciar o ciclo de vida do das mesas
 * 
 * Este Service NÃO sabe nada sobre HTTP, Apenas processa e lança exceções de
 * negócio.
 */
@Service
public class MesaService {

	private final PedidoRepository pedidoRepository;
	private final MesaRepository mesaRepository;

	public MesaService(MesaRepository mesaRepository, PedidoRepository pedidoRepository) {

		this.mesaRepository = mesaRepository;
		this.pedidoRepository = pedidoRepository;
	}

	// ============================================================
	// CADASTRO DE MESA
	// ============================================================
	@Transactional
	public Mesa cadastrarMesa(Integer numero, Integer capacidade) {

		// VALIDAÇÕES SEM ACESSO AO BANCO
		if (numero == null || numero <= 0) {
			throw new IllegalArgumentException("O numero da mesa deve ser um valor positivo");
		}

		/*
		 * Capacidade pode ser null (não informada), aí assumimos a capacidade padrão de
		 * 4 lugares
		 */
		if (capacidade == null) {
			capacidade = 4;
		}

		// VALIDAÇÕES COM BANCO DE DADOS
		mesaRepository.findByNumero(numero).ifPresent(mesaExistente -> {
			throw new MesaJaCadastradaException(numero);
		});

		// Instaciar e popular objeto com dados validados
		Mesa mesa = new Mesa();
		mesa.setNumero(numero);
		mesa.setCapacidade(capacidade);
		mesa.setAtiva(true);

		return mesaRepository.save(mesa);
	}

	// ============================================================
	// OCUPAR MESA
	// ============================================================
	@Transactional
	public Mesa ocuparMesa(Integer numeroMesa) {

		// Fazer busca pelo numero
		Mesa mesa = buscarPorNumero(numeroMesa);

		// Verifica se a mesa está ativa
		if (!mesa.isAtiva()) {
			throw new NegocioException("A mesa" + numeroMesa + " está inativa e não pode ser ocupada.");
		}

		// Chama Metodo da Model que sabe como ocupar uma mesa
		mesa.ocupar();

		// Persiste a mudança de estado
		return mesaRepository.save(mesa);
	}

	// ============================================================
	// LIBERAR MESA
	// ============================================================
	@Transactional
	public Mesa liberarMesa(Integer numeroMesa) {

		Mesa mesa = buscarPorNumero(numeroMesa);

		// Verificar Pedido Pendentes nessa mesa

		// Definindo aqui o que NÃO queremos pedidos encerrados
		List<StatusPedido> statusFechados = List.of(StatusPedido.FINALIZADO, StatusPedido.CANCELADO,
				StatusPedido.SAIU_PARA_ENTREGA);

		// Chamando o repository passando os filtros "fixos" da regra de negócio
		List<Pedido> pedidosAtivos = pedidoRepository.findByTipoAndNumeroMesaAndStatusNotIn(TipoPedido.MESA, numeroMesa,
				statusFechados);

		if (!pedidosAtivos.isEmpty()) {
			throw new NegocioException("Não é possivel liberar a mesa " + numeroMesa + ". Existe(m) "
					+ pedidosAtivos.size() + " pedido(s) em andamento");
		}

		mesa.liberar();

		return mesaRepository.save(mesa);

	}

	// ============================================================
	// CONSULTAS
	// ============================================================

	// Busca Mesa por numero
	@Transactional(readOnly = true)
	public Mesa buscarPorNumero(Integer numero) {
		return mesaRepository.findByNumero(numero).orElseThrow(() -> new MesaNaoEncontradaException(numero));
	}

	// Busca Mesa por ID
	@Transactional(readOnly = true)
	public Mesa buscarMesaPorId(Integer id) {
		return mesaRepository.findById(id).orElseThrow(() -> new MesaNaoEncontradaException(id));
	}

	// Listar todas as mesas
	@Transactional(readOnly = true)
	public List<Mesa> listarTodasAsMesas() {
		return mesaRepository.findAllByOrderByNumeroAsc();
	}

	// Listar mesas livres
	@Transactional(readOnly = true)
	public List<Mesa> listarMesasLivres() {
		return mesaRepository.findByOcupadaFalseAndAtivaTrueOrderByNumeroAsc();
	}

	// Listar mesas ocupadas
	@Transactional(readOnly = true)
	public List<Mesa> listarMesasOcupadas() {
		return mesaRepository.findAllByOrderByNumeroAsc().stream()
				.filter(mesa -> mesa.isOcupada() && mesa.isAtiva())
				.collect(Collectors.toList());
	}

	// ============================================================
	// MANUTENÇÃO DE MESAS
	// ============================================================

	/**
	 * ATIVAR/DESATIVAR MESA (manutenção)
	 * 
	 * Mesas podem ser desativadas temporariamente (ex: quebrou uma cadeira) sem
	 * precisar excluir do sistema (mantém histórico).
	 */
	@Transactional
	public void alterarStatusMesa(Integer numeroMesa, Boolean novoStatus) {

		Mesa mesaStatus = buscarPorNumero(numeroMesa);

		// Objects.equals previne NullPointerException se novoStatus ou
		// mesaStatus.isAtiva() forem null
		// Só entra no bloco se o status for DIFERENTE do atual
		if (!Objects.equals(mesaStatus.isAtiva(), novoStatus)) {
			
			// Não pode alterar o status de uma mesa ocupada e ativa
			if (mesaStatus.isOcupada() && mesaStatus.isAtiva()) {
				throw new NegocioException(
						"Não é possível alterar o status da mesa " + mesaStatus.getNumero() + " pois está ocupada.");
			}
			
			mesaStatus.setAtiva(novoStatus);
		}

		mesaRepository.save(mesaStatus);
	}

	// ============================================================
	// VERIFICAÇÕES RÁPIDAS
	// ============================================================

	/**
	 * VERIFICAR SE MESA EXISTE
	 */
	@Transactional(readOnly = true)
	public boolean existeMesa(Integer numero) {
		return mesaRepository.existsByNumero(numero);
	}
	
	// =============================================================
	//  MÉTODOS DTO (conversão dentro da transação)
	// =============================================================
	
	@Transactional
	public MesaResponse cadastrarMesaResponse(MesaCadastroRequest request) {
		
		Mesa mesa = cadastrarMesa(request.numero(), request.capacidade());
		
		return MesaResponse.from(mesa);
	}
	
	@Transactional
	public MesaResponse ocuparMesaResponse(Integer numeroMesa) {
		
		Mesa mesa = ocuparMesa(numeroMesa);
		return MesaResponse.from(mesa);
		
	}
	
	@Transactional
	public MesaResponse liberarMesaResponse(Integer numeroMesa) {
		
		Mesa mesa = liberarMesa(numeroMesa);
		return MesaResponse.from(mesa);
		
	}
	
	@Transactional
	public void alterarStatusMesaResponse(Integer numeroMesa, MesaStatusRequest request ) {
		
		alterarStatusMesa(numeroMesa, request.ativa());
		
	}

	
	@Transactional(readOnly = true)
	public MesaResponse buscarPorNumeroResponse(Integer numero) {
		
		return MesaResponse.from(buscarPorNumero(numero));
		
	}

	@Transactional(readOnly = true)
	public MesaResponse buscarMesaPorIdResponse(Integer id) {
		
		return MesaResponse.from(buscarMesaPorId(id));
		
	}

	@Transactional(readOnly = true)
	public List<MesaResponse> listarTodasAsMesaResponse(){
		
		return listarTodasAsMesas().stream()
				.map(MesaResponse::from)
				.toList();
		
	}

	@Transactional(readOnly = true)
	public List<MesaResponse> listarMesasLivresResponse() {
		
		return listarMesasLivres().stream()
				.map(MesaResponse::from)
				.toList();
		
	}

	@Transactional(readOnly = true)
	public List<MesaResponse> listarMesasOcupadasResponse() {
		
		return listarMesasOcupadas().stream()
				.map(MesaResponse::from)
				.toList();
		
	}
	
	
}
