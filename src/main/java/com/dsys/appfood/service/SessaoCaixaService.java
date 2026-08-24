package com.dsys.appfood.service;

import com.dsys.appfood.repository.SessaoCaixaRepository;
import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dsys.appfood.config.AutorizacaoUtil;
import com.dsys.appfood.domain.enums.StatusCaixa;
import com.dsys.appfood.domain.model.SessaoCaixa;
import com.dsys.appfood.domain.model.Caixa;
import com.dsys.appfood.domain.model.MovimentacaoCaixa;
import com.dsys.appfood.domain.model.Usuario;
import com.dsys.appfood.dto.request.SessaoCaixaAbrirRequest;
import com.dsys.appfood.dto.request.SessaoCaixaEstornoRequest;
import com.dsys.appfood.dto.request.SessaoCaixaFecharRequest;
import com.dsys.appfood.dto.request.SessaoCaixaSangriaRequest;
import com.dsys.appfood.dto.response.SessaoCaixaResponse;
import com.dsys.appfood.dto.response.SessaoCaixaStatusResponse;
import com.dsys.appfood.dto.response.MovimentacaoCaixaResponse;
import com.dsys.appfood.exception.SemSessaoAbertaException;
import com.dsys.appfood.exception.CaixaNaoEncontradoException;
import com.dsys.appfood.exception.EntidadeNaoEncontradaException;
import com.dsys.appfood.exception.NegocioException;

import com.dsys.appfood.exception.SessaoCaixaNaoEncontradaException;
import com.dsys.appfood.repository.CaixaRepository;
import com.dsys.appfood.repository.MovimentacaoCaixaRepository;

/**
 * Classe responsavel por orquestar todas as regras de negócio relacionadas a
 * Sessao do Caixa
 *
 * Responsabilidade ÚNICA: gerenciar o ciclo de vida das Sessões de caixa físico (abertura ->
 * movimentação -> fechamento)
 *
 * Separação de Responsabilidades:
 * Este Service cuida das SESSÕES, não dos caixas físicos nem das movimentações.
 * 
 * Este Service NÃO sabe nada sobre HTTP, Apenas processa e lança exceções de
 * negócio.
 */
@Service
public class SessaoCaixaService {

	private final SessaoCaixaRepository sessaoCaixaRepository;
	// INJEÇÃO DAS DEPENDENCIAS usadas no Service, apenas 4
	private final CaixaRepository caixaRepository;
	private final UsuarioService usuarioService;
	private final MovimentacaoCaixaRepository movimentacaoRepository;

	// Injeção via construtor - campos final garatem a imutabilidade
	public SessaoCaixaService(CaixaRepository caixaRepository, UsuarioService usuarioService,
			MovimentacaoCaixaRepository movimentacaoRepository, MovimentacaoCaixaService movimentacaoCaixaService, SessaoCaixaRepository sessaoCaixaRepository) {
		this.sessaoCaixaRepository = sessaoCaixaRepository;
		this.caixaRepository = caixaRepository;
		this.usuarioService = usuarioService;
		this.movimentacaoRepository = movimentacaoRepository;
	}

	// ============================================================================
	// 1º Ação do CAIXA - Abrir SessaoCaixa
	// Regaras da Ação: gerente Autoriza | operador sem caixa aberto | valor >= 0
	// ============================================================================

	@Transactional
	public SessaoCaixa abrirCaixa(Integer caixaId, Integer operadorId, String loginGerente, 
									String senhaGerente, BigDecimal valorInicial) {

		// REGRA: valor inicial não pode ser negativo
		// (pode ser zerado - caixa abre sem troco)
		if (valorInicial == null) {
			valorInicial = BigDecimal.ZERO; // se o valor não for informado, assume zero
		}

		if (valorInicial.signum() == -1) {
			throw new IllegalArgumentException("O valor inicial do caixa não pode ser negativo.");
		}
		
		// Busca o Caixa Físico 
		Caixa caixa = caixaRepository.findById(caixaId)
				.orElseThrow(() -> new CaixaNaoEncontradoException(caixaId)); 
		
		// Valida se o caixa físico está ativo
		if(!caixa.isAtivo()) {
			throw new NegocioException("Este caixa está inativo.");
		}

		// Busca o Operador - Lança exeção clara se não existir
		Usuario operador = usuarioService.buscaPorId(operadorId);

		// REGRA: quem autoriza precisa ser Gerente ou ADM
		// Sem isso, qualquer operador poderia "autorizar" a si mesmo
		// Autentica e valida o gerente — tudo dentro do UsuarioService
		Usuario gerente = usuarioService.autenticarGerente(loginGerente, senhaGerente);

		// REGRA: Um caixa físico não pode ter mais de uma sessão aberta
		boolean caixaEstaAberto = sessaoCaixaRepository.existsByCaixaAndStatus(caixa, StatusCaixa.ABERTO);

		if (caixaEstaAberto) {
			throw new NegocioException("Já existe uma sessão aberta para o caixa " + caixa.getNome());
		}		
		
		// REGRA: operador não pode abrir um segundo caixa se ja tem um aberto
		boolean temCaixaAberto = sessaoCaixaRepository.existsByOperadorAndStatus(operador, StatusCaixa.ABERTO);
		
		if(temCaixaAberto) {
			throw new NegocioException("O operador " + operador.getNome() + " já tem caixa aberto.");
		}

		// Cria a sessão usando o CONSTRUTOR
		SessaoCaixa sessaoCaixa = new SessaoCaixa(caixa, operador, valorInicial);
		sessaoCaixa.setGerente(gerente);
		
		// Persiste a sessão
		sessaoCaixaRepository.save(sessaoCaixa);
		
		// Cria a movimentação de abertura		
		MovimentacaoCaixa abertura = MovimentacaoCaixa.criarAberturaCaixa(sessaoCaixa);

		// Persiste a movimentação de abertura
		movimentacaoRepository.save(abertura);
		
		// Retorna a Sessão 
		return sessaoCaixa;
	}

	// ============================================================================
	// 2º Ação do CAIXA - Registrar Venda (Entrada na SessaoCaixa)
	// Regaras da Ação: O caixa deve estar aberto | valor > 0
	// ============================================================================

	@Transactional
	public MovimentacaoCaixa registrarVenda(Integer sessaoId, Integer pedidoId, BigDecimal valorPago) {
		// Validação sem acessar o banco
		if (valorPago == null || valorPago.compareTo(BigDecimal.ZERO) <= 0) {
			throw new NegocioException("Valor do pagamento deve ser positivo.");
		}

		// Busca a Sessao Aberta 
		SessaoCaixa sessaoCaixa = buscaSessaoAberta(sessaoId); // Utilizando o método privado

		// Cria a movimentação usando o Static Factory Method da Model
		MovimentacaoCaixa entrada = MovimentacaoCaixa.criarEntradaCaixa(sessaoCaixa, valorPago, pedidoId);

		// O saldo é atualizado dentra da chamada criarEntradaCaixa

		// Salva a movimentação e o caixa atualizado
		sessaoCaixaRepository.save(sessaoCaixa);
		
		return movimentacaoRepository.save(entrada);
	}

	// ================================================================================
	// 3º Ação do CAIXA - Realizar Sangria (saída autorizada pelo gerente)
	// Regaras da Ação: gerente autoriza | caixa ABERTO | valor > 0 | saldo
	// suficiente
	// ================================================================================

	@Transactional
	public MovimentacaoCaixa realizarSangria(Integer sessaoId, String loginGerente, String senhaGerente,
			BigDecimal valor, String motivo) {
		// Validações sem acessar o banco
		if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
			throw new NegocioException("O valor não pode ser negativo");
		}

		if (motivo == null || motivo.isBlank()) {
			throw new NegocioException("Informe o motivo da sangria");
		}

		// Autentica o Gerente
		Usuario gerente = usuarioService.autenticarGerente(loginGerente, senhaGerente);

		// Busca a Sessão aberta do caixa
		SessaoCaixa sessaoCaixa = buscaSessaoAberta(sessaoId);

		// REGRA: não pode fazer sangria maior que o saldo atual
		if (valor.compareTo(sessaoCaixa.getSaldo()) > 0) {
			throw new NegocioException("Valor da sangria (R$ " + valor + ") excede o saldo atual do caixa (R$ "+ sessaoCaixa.getSaldo() +").");
		}

		// Usa o Static Factory Method já valida gerente e valor
		MovimentacaoCaixa sangria = MovimentacaoCaixa.criarSaidaSangria(sessaoCaixa, valor, gerente, "SANGRIA: " + motivo);

		// O saldo é atualizado no proprio metodo de sangria.

		sessaoCaixaRepository.save(sessaoCaixa);
		return movimentacaoRepository.save(sangria);
	}

	// ================================================================================
	// 4º Ação do CAIXA - Realizar Estorno de lançamento no SessaoCaixa (saída autorizada
	// pelo gerente)
	// Regaras da Ação: gerente autoriza | caixa ABERTO | valor > 0 | saldo
	// suficiente
	// ================================================================================

	@Transactional
	public MovimentacaoCaixa realizarEstorno(Integer movimentacaoId, String loginGerente, String senhaGerente,
			String motivo) {
		// Validações sem acessar o banco
		if (motivo == null || motivo.isBlank()) {
			throw new IllegalArgumentException("Informe o motivo do estorno");
		}

		// Busca Movimentação
		MovimentacaoCaixa mov = movimentacaoRepository.findById(movimentacaoId)
				.orElseThrow(() -> new EntidadeNaoEncontradaException("Lançamento não encontrado", movimentacaoId));

		// Autentica o Gerente
		Usuario gerente = usuarioService.autenticarGerente(loginGerente, senhaGerente);
		
		return executarEstorno(mov, gerente, motivo);

	}

	// ================================================================================
	// 5º Ação do CAIXA - Fechar SessaoCaixa
	// Regaras da Ação: caixa ABERTO | gerente autoriza | sangria automática do
	// saldo
	// ================================================================================

	@Transactional
	public SessaoCaixa fecharCaixa(Integer sessaoId, String loginGerente, String senhaGerente, BigDecimal valorContado) {
		
		// Busca gerente
		Usuario gerente = usuarioService.autenticarGerente(loginGerente, senhaGerente);

		// Busca Sessão aberta do caixa a ser fechado
		SessaoCaixa sessaoCaixa = buscaSessaoAberta(sessaoId);

		// Criar o registro de fechamento - Fechamento de Caixa não é retirada, apenas registro 
			MovimentacaoCaixa fechamento = MovimentacaoCaixa.criarFechamento(sessaoCaixa, valorContado, gerente);

			movimentacaoRepository.save(fechamento);

		//  sessaoCaixa.fechar(gerente, valorFechamento) não está mais aqui porque ele já está sendo chamado de dentro do metodo criarFechamento


		return sessaoCaixaRepository.save(sessaoCaixa);

	}
	
	

	// =========================================================
	// CONSULTAR MOVIMENTAÇÕES
	// Somente Leitura
	// =========================================================

	@Transactional(readOnly = true)
	public List<MovimentacaoCaixa> consultaMovimentacoes(Integer sessaoId) {
		// Confirma que o caixa existe antes de buscar movimentações
		SessaoCaixa sessaoCaixa =  sessaoCaixaRepository.findById(sessaoId)
				.orElseThrow(() -> new SessaoCaixaNaoEncontradaException(sessaoId));

		return movimentacaoRepository.findBySessaoCaixaOrderByDataHoraMovimentoDesc(sessaoCaixa);
	}

	// =========================================================
	// MÉTODO BUSCAR CAIXA ABERTO
	// evita repetição nos métodos que precisam dessa verificação
	// =========================================================
	@Transactional(readOnly = true)
	public SessaoCaixa buscaSessaoAberta(Integer sessaoId) {
		SessaoCaixa sessaoCaixa = sessaoCaixaRepository.findById(sessaoId)
				.orElseThrow(() -> new SessaoCaixaNaoEncontradaException(sessaoId));
		if (sessaoCaixa.getStatus() != StatusCaixa.ABERTO) {
			throw new SemSessaoAbertaException(sessaoCaixa.getCaixa().getNome());
		}

		return sessaoCaixa;
	}

	/**
	 * Lista todas as sessões de Caixa abertas.
	 *
	 */
	@Transactional(readOnly = true)
	public Page<SessaoCaixa> listarSessoesAbertas(Pageable pageable) {
		
		return sessaoCaixaRepository.findByStatus(StatusCaixa.ABERTO, pageable);
		
	}

	/**
	 * Método de estorno interno usado por outros serviços
	 */

	@Transactional
	public MovimentacaoCaixa realizarEstornoInterno(Integer movimentacaoId, Usuario gerente, String motivo) {
	    MovimentacaoCaixa mov = movimentacaoRepository.findById(movimentacaoId)
	            .orElseThrow(() -> new EntidadeNaoEncontradaException("Lançamento não encontrado", movimentacaoId));

	    AutorizacaoUtil.exigirPapelGerente(gerente);

	    return executarEstorno(mov, gerente, motivo);
	}
	
	
	/**
	 * Lógica compartilhada entre a rota externa (autenticação por senha) e a
	 * rota interna (usuário já resolvido por outro serviço).
	 *
	 */
	private MovimentacaoCaixa executarEstorno(MovimentacaoCaixa mov, Usuario gerente, String motivo) {
		
		// Busca o caixa aberto
				SessaoCaixa sessaoCaixa = buscaSessaoAberta(mov.getSessaoCaixa().getId());

				// Usa o Static Factory Method já valida gerente e valor
				MovimentacaoCaixa estorno = MovimentacaoCaixa.criarEstorno(mov, gerente,motivo);

				// O saldo é atualizado no proprio metodo de estorno.

				sessaoCaixaRepository.save(sessaoCaixa);
				movimentacaoRepository.save(estorno);
				
				return estorno;
	}

	// =============================================================
	// MÉTODOS DTO (conversão dentro da transação)
	// =============================================================
	@Transactional (readOnly = true)
	public Page<SessaoCaixaStatusResponse> listarSessoesAbertasResponse(Pageable pageable) {

		return listarSessoesAbertas(pageable).map(SessaoCaixaStatusResponse::deSessaoAberta);

	}

	@Transactional
	public SessaoCaixaResponse abrirCaixaResponse(SessaoCaixaAbrirRequest request) {

		SessaoCaixa sessaoCaixa = abrirCaixa(request.caixaId(), request.operadorId(), request.loginGerente(), request.senhaGerente(), 
								request.valorInicial());

		return SessaoCaixaResponse.from(sessaoCaixa);

	}

	@Transactional
	public SessaoCaixaResponse fecharCaixaResponse( SessaoCaixaFecharRequest request) {

		SessaoCaixa sessaoCaixa = fecharCaixa(request.sessaoCaixaId(), request.loginGerente(), request.senhaGerente(), request.valorFinal());

		return SessaoCaixaResponse.from(sessaoCaixa);

	}

	@Transactional
	public MovimentacaoCaixaResponse realizarSangriaResponse(SessaoCaixaSangriaRequest request) {

		MovimentacaoCaixa mov = realizarSangria(request.sessaoCaixaId(), request.loginGerente(), request.senhaGerente(),
				request.valor(), request.motivo());

		return MovimentacaoCaixaResponse.from(mov);
	}

	@Transactional
	public MovimentacaoCaixaResponse realizarEstornoResponse(Integer movimentacaoId, SessaoCaixaEstornoRequest request) {

		MovimentacaoCaixa mov = realizarEstorno(
				movimentacaoId,
				request.loginGerente(),
				request.senhaGerente(),
				request.motivo());

		return MovimentacaoCaixaResponse.from(mov);
	}

}
