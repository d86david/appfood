package com.dsys.appfood.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import com.dsys.appfood.domain.enums.StatusCaixa;
import com.dsys.appfood.domain.model.Caixa;
import com.dsys.appfood.domain.model.MovimentacaoCaixa;
import com.dsys.appfood.domain.model.Usuario;
import com.dsys.appfood.exception.CaixaFechadoException;
import com.dsys.appfood.exception.CaixaNaoEncontradoException;
import com.dsys.appfood.exception.NegocioException;
import com.dsys.appfood.repository.CaixaRepository;
import com.dsys.appfood.repository.MovimentacaoCaixaRepository;

import org.springframework.transaction.annotation.Transactional;

/**
 * Classe responsavel por orquestar todas as regras de negócio relacionadas ao Caixa
 * 
 * Responsabilidade ÚNICA: gerencias o ciclo de vida do Caixa (abertura -> movimentação -> fechamento)
 * 
 * Este Service NÃO sabe nada sobre HTTP, Apenas processa e lança exceções de negócio.
 */
@Service
public class CaixaService {
	
	//INJEÇÃO DAS DEPENDENCIAS usadas no Service, apenas 3 
	private final CaixaRepository caixaRepository;
	private final UsuarioService usuarioService;
	private final MovimentacaoCaixaRepository movimentacaoRepository;
	
	//Injeção via construtor - campos final garatem a imutabilidade
	public CaixaService(CaixaRepository caixaRepository, 
						UsuarioService usuarioService, 
						MovimentacaoCaixaRepository movimentacaoRepository ) {
		this.caixaRepository = caixaRepository;
		this.usuarioService = usuarioService;
		this.movimentacaoRepository = movimentacaoRepository;
	}
	
	//============================================================================
	// 1º Ação do CAIXA - Abrir Caixa 
	// Regaras da Ação: gerente Autoriza | operador sem caixa aberto | valor >= 0
	//============================================================================
	
	@Transactional
	public Caixa abrirCaixa(Integer operadorId, 
							String loginGerente, 
							String senhaGerente, 
							BigDecimal valorInicial) {
		
		//REGRA: valor inicial não pode ser negativo
		// (pode ser zerado - caixa abre sem troco)
		if(valorInicial == null) {
			valorInicial = BigDecimal.ZERO; //se o valor não for informado, assume zero
		}
		
		if(valorInicial.signum() == -1) {
			throw new IllegalArgumentException("O valor inicial do caixa não pode ser negativo.");
		}
		
		//Busca o Operador - Lança exeção clara se não existir
		Usuario operador = usuarioService.buscaPorId(operadorId);
		
		//REGRA: quem autoriza é precisa ser Gerente ou ADM
		//Sem isso, qualquer operador poderia "autorizar" a si mesmo
		// Autentica e valida o gerente — tudo dentro do UsuarioService
		Usuario gerente = usuarioService.autenticarGerente(loginGerente, senhaGerente);
		
		//REGRA: operador não pode abrir um segundo caixa se ja tem um aberto
		boolean temCaixaAberto = caixaRepository
				.findByOperadorAndStatus(operador, StatusCaixa.ABERTO)
				.isPresent();
		
		if(temCaixaAberto) {
			throw new NegocioException(
					"O operador "+ operador.getNome() +" já possui um caixa aberto.");
		}
		
		//Cria um novo caixa, que começa fechado por padrão
		Caixa caixa = new Caixa();
		
		//Chama o método de Abrir caixa da Model
		caixa.abrirCaixa(valorInicial, gerente);
		caixa.setOperador(operador);
		
		// Persiste e retorna
		return caixaRepository.save(caixa);	
	}
	
	//============================================================================
	// 2º Ação do CAIXA - Registrar Venda (Entrada no Caixa)
	// Regaras da Ação: O caixa deve estar aberto | valor > 0
	//============================================================================

	@Transactional
	public MovimentacaoCaixa registrarVenda(Integer caixaId, Integer pedidoId,
											BigDecimal valorPago) {
		//Validação sem acessar o banco 
		if(valorPago == null || valorPago.compareTo(BigDecimal.ZERO) <= 0) {
			throw new NegocioException("Valor do pagamento deve ser positivo.");
		}
		
		//Busca o caixa
		Caixa caixa = buscaCaixaAberto(caixaId); //Utilizando o método privado
		
		// Cria a movimentação usando o Static Factory Method da Model
		MovimentacaoCaixa entrada = MovimentacaoCaixa.criarEntradaCaixa(caixa, valorPago, pedidoId);
		
		// Atualiza o saldo — chama o método da Model (que já valida se está aberto)
		caixa.atualizarSaldo(valorPago, entrada.getTipo());
		
		// Salva a movimentação e o caixa atualizado
		caixaRepository.save(caixa);
		return movimentacaoRepository.save(entrada);
	}
	
	//================================================================================
	// 3º Ação do CAIXA - Realizar Sangria (saída autorizada pelo gerente)
	// Regaras da Ação: gerente autoriza | caixa ABERTO | valor > 0 | saldo suficiente
	//================================================================================
	
	@Transactional
	public MovimentacaoCaixa realizarSangria(Integer caixaId, 
											String loginGerente, String senhaGerente,
											BigDecimal valor, String motivo) {
		//Validações sem acessar o banco
		if(valor == null || valor.compareTo(BigDecimal.ZERO) <= 0 ) {
			throw new IllegalArgumentException("O valor não pode ser negativo");
		}
		
		if (motivo == null || motivo.isBlank()) {
			throw new IllegalArgumentException("Informe o motivo da sangria");
		}
		
		//Autentica o Gerente
		Usuario gerente = usuarioService.autenticarGerente(loginGerente, senhaGerente);
		
		//Busca o caixa aberto
		Caixa caixa = buscaCaixaAberto(caixaId);
		
		// REGRA: não pode fazer sangria maior que o saldo atual
		if (valor.compareTo(caixa.getSaldo()) > 0) {
			throw new NegocioException(
					"Valor da sangria (R$" + valor + ") excede o saldo atual do caixa.");
		}
		
		// Usa o Static Factory Method já valida gerente e valor
		MovimentacaoCaixa sangria = MovimentacaoCaixa.criarSaidaSangria(caixa, valor, gerente, motivo);
		
		//Atualiza o saldo do caixa
		caixa.atualizarSaldo(valor, sangria.getTipo());
		
		caixaRepository.save(caixa);
		return movimentacaoRepository.save(sangria);
	}
	
	//================================================================================
	// 4º Ação do CAIXA - Fechar Caixa
	// Regaras da Ação: caixa ABERTO | gerente autoriza | sangria automática do saldo
	//================================================================================
	
	@Transactional
	public Caixa fecharCaixa(Integer caixaId, String loginGerente, String senhaGerente) {
		//Busca gerente
		Usuario gerente = usuarioService.autenticarGerente(loginGerente, senhaGerente);
		
		//Busca o caixa - deve estar aberto
		Caixa caixa = buscaCaixaAberto(caixaId);
		
		// REGRA: ao fechar, se houver saldo, registra sangria automática
        // (o dinheiro sai do caixa fisicamente no fechamento)
		if(caixa.getSaldo().compareTo(BigDecimal.ZERO) > 0) {
			MovimentacaoCaixa sangriaFechamento = MovimentacaoCaixa.criarSaidaSangria(
					caixa, 
					caixa.getSaldo(), 
					gerente, 
					"Sangria automática no fechamento do caixa"
					);
			
			//Deduzir o saldo do caixa atual usando o tipo da movimentação
			caixa.atualizarSaldo(caixa.getSaldo(), sangriaFechamento.getTipo());
			
			movimentacaoRepository.save(sangriaFechamento);
		}
		
		//Delega o fechamento para a propria Model, que ja sabe o que fazer
		caixa.fecharCaixa(gerente);
		
		return caixaRepository.save(caixa);
		
	}
	
	//=========================================================
	//5º Ação do CAIXA - CONSULTAR MOVIMENTAÇÕES
	//Somente Leitura
	//=========================================================
	
	@Transactional(readOnly = true)
	public List<MovimentacaoCaixa> consMovimentacaoCaixas(Integer caixaId){
		// Confirma que o caixa existe antes de buscar movimentações
		caixaRepository.findById(caixaId)
			.orElseThrow(() -> new CaixaNaoEncontradoException(caixaId));
		
		return movimentacaoRepository.findByCaixaId(caixaId);
	}
	
	//=========================================================
	//MÉTODO PRIVADO - reutilizado internamente, 
	//evita repetição nos métodos que precisam dessa verificação
	//=========================================================
	private Caixa buscaCaixaAberto(Integer caixaId) {
		Caixa caixa = caixaRepository.findById(caixaId)
				.orElseThrow(() -> new CaixaNaoEncontradoException(caixaId));
		if(caixa.getStatus() != StatusCaixa.ABERTO) {
			throw new CaixaFechadoException(caixaId);
		}
		
		return caixa;
	}
}
