package com.dsys.appfood.service;

import com.dsys.appfood.domain.enums.FormaPagamento;
import com.dsys.appfood.domain.enums.TipoConta;
import com.dsys.appfood.domain.enums.TipoMovimentacao;
import com.dsys.appfood.domain.model.ConfiguracaoPagamento;
import com.dsys.appfood.domain.model.ContaCorrente;
import com.dsys.appfood.domain.model.MovimentacaoContaCorrente;
import com.dsys.appfood.domain.model.Pagamento;
import com.dsys.appfood.domain.model.Usuario;
import com.dsys.appfood.exception.ContaJaCadastradaException;
import com.dsys.appfood.exception.ContaNaoEncontradaException;
import com.dsys.appfood.exception.NegocioException;
import com.dsys.appfood.repository.ConfiguracaoPagamentoRepository;
import com.dsys.appfood.repository.ContaCorrenteRepository;
import com.dsys.appfood.repository.MovimentacaoContaCorrenteRepository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Classe responsavel por orquestar todas as regras de negócio relacionadas a
 * Conta Corrente
 * 
 * Responsabilidade ÚNICA: gerenciar o ciclo de vida da Conta Corrente
 * 
 * Este Service NÃO sabe nada sobre HTTP, Apenas processa e lança exceções de
 * negócio.
 */
@Service
public class ContaCorrenteService {

	private final ConfiguracaoPagamentoRepository configuracaoRepository;
	private final MovimentacaoContaCorrenteRepository movimentacaoRepository;
	private final ContaCorrenteRepository contaRepository;

	public ContaCorrenteService(ContaCorrenteRepository contaRepository,
			MovimentacaoContaCorrenteRepository movimentacaoRepository, ConfiguracaoPagamentoRepository configuracaoRepository) {
		this.contaRepository = contaRepository;
		this.movimentacaoRepository = movimentacaoRepository;
		this.configuracaoRepository = configuracaoRepository;
	}

	// ============================================================
	// CADASTRO
	// ============================================================
	@Transactional
	public ContaCorrente cadastrarConta(String nome, TipoConta tipo, String banco, String codBanco, String agencia,
			String conta) {

		// VALIDAÇÕES SEM BANCO
		if (nome == null || nome.isEmpty()) {
			throw new IllegalArgumentException("O nome da conta deve ser informado");
		}


		// PADRONIZAÇÃO DOS CAMPOS
		String nomePadronizado = nome.trim();
		String bancoPadronizado = banco.trim();
		String codBancoPadronizado = codBanco.trim();
		String agenciaPadronizada = agencia.trim();
		String contaPadronizada = conta.trim();

		// Verifica se ja existe conta como nome cadastrada
		if (contaRepository.findByNomeIgnoreCase(nomePadronizado).isPresent()) {
			throw new ContaJaCadastradaException(nomePadronizado);
		}

		// Inatancia a conta
		ContaCorrente nova = new ContaCorrente(nomePadronizado, tipo);
		nova.setCodBanco(codBancoPadronizado);
		nova.setBanco(bancoPadronizado);
		nova.setAgencia(agenciaPadronizada);
		nova.setConta(contaPadronizada);

		return contaRepository.save(nova);
	}

	// ============================================================
	// EDIÇÃO
	// ============================================================
	@Transactional
	public ContaCorrente editarConta(Integer id, String novoNome, String novoBanco, String novoCodBanco,
			String novaAgencia, String novaConta) {

		ContaCorrente contaExistente = contaRepository.findById(id).orElseThrow(() -> new ContaNaoEncontradaException(id));

		// VALIDAÇÕES SEM BANCO
		if (novoNome == null || novoNome.isEmpty()) {
			throw new IllegalArgumentException("O nome da conta deve ser informado");
		}


		// PADRONIZAÇÃO DOS CAMPOS
		String nomePadronizado = novoNome.trim();
		String bancoPadronizado = novoBanco.trim();
		String codBancoPadronizado = novoCodBanco.trim();
		String agenciaPadronizada = novaAgencia.trim();
		String contaPadronizada = novaConta.trim();

		contaRepository.findByNomeIgnoreCase(nomePadronizado).ifPresent(existente -> {
			if (!existente.getId().equals(id)) {
				throw new ContaJaCadastradaException(nomePadronizado, id);
			}
		});

		// seta os campos
		contaExistente.setNome(nomePadronizado);
		contaExistente.setCodBanco(codBancoPadronizado);
		contaExistente.setBanco(bancoPadronizado);
		contaExistente.setAgencia(agenciaPadronizada);
		contaExistente.setConta(contaPadronizada);

		return contaRepository.save(contaExistente);
	}

	// =============================================================
	// ATIVAÇÃO E INATIVAÇÃO
	// =============================================================

	// Ativar
	@Transactional
	public void ativarConta(Integer id) {

		// Busca Conta e lança exceção se não existir
		ContaCorrente conta = contaRepository.findById(id).orElseThrow(() -> new ContaNaoEncontradaException(id));

		// Confirma se conta está inativa
		if (conta.isAtiva()) {
			throw new NegocioException("Conta já ativa id: " + id);
		}

		conta.ativarConta();

		contaRepository.save(conta);

	}

	// Inativar
	@Transactional
	public void inativaConta(Integer id) {

		// Busca Conta e lança exceção se não existir
		ContaCorrente conta = contaRepository.findById(id).orElseThrow(() -> new ContaNaoEncontradaException(id));

		// Confirma se conta está ativa
		if (!conta.isAtiva()) {
			throw new NegocioException("Conta já inativa id: " + id);
		}

		conta.inativar();

		contaRepository.save(conta);

	}

	// =============================================================
	// MOVIMENTAÇÕES
	// =============================================================

	//ENTRADA
	@Transactional
	public MovimentacaoContaCorrente registrarEntrada(Integer contaId, BigDecimal valor, String descricao,
			Usuario usuario, Pagamento pagamentoRelacionado) {
		
		ContaCorrente conta = buscarPorId(contaId);
		
		//Verifica se conta está ativa
		if (!conta.isAtiva()) {
			throw new NegocioException("Conta inativa não pode receber movimentações.");
		}

		conta.creditar(valor);
		contaRepository.save(conta);

		MovimentacaoContaCorrente mov = new MovimentacaoContaCorrente(conta, TipoMovimentacao.ENTRADA, valor, descricao,
				usuario);
		mov.setPagamento(pagamentoRelacionado);
		return movimentacaoRepository.save(mov);
	}

	//SAÍDA
	@Transactional
	public MovimentacaoContaCorrente registrarSaida(Integer contaId, BigDecimal valor, String descricao,
			Usuario usuario) {
		
		ContaCorrente conta = buscarPorId(contaId);
		conta.debitar(valor);
		contaRepository.save(conta);

		MovimentacaoContaCorrente mov = new MovimentacaoContaCorrente(conta, TipoMovimentacao.SAIDA, valor, descricao,
				usuario);
		return movimentacaoRepository.save(mov);
	}
	
	// =============================================================
	// MÉTODOS UTILITÁRIOS
	// =============================================================
	
	/**
	 * Recupera a conta corrente vinculada a uma forma de pagamento.
	 * O método utiliza a tabela de configuração para determinar o destino dos fundos.
	 * Caso não exista uma regra cadastrada no banco, uma exceção de negócio é lançada.
	 */
	@Transactional(readOnly = true)
	public ContaCorrente getContaPadraoParaformaPagamento(FormaPagamento forma) {
		return configuracaoRepository.findByFormaPagamento(forma)
				.map(ConfiguracaoPagamento::getContaCorrente)
				.orElseThrow(() -> new NegocioException(
						String.format("Nenhuma conta corrente associada à forma de pagamento: %s", forma)));
	}

	// =============================================================
	// BUSCAS
	// =============================================================
	@Transactional(readOnly = true)
	public ContaCorrente buscarPorId(Integer id) {
		return contaRepository.findById(id).orElseThrow(() -> new ContaNaoEncontradaException(id));
	}

	@Transactional(readOnly = true)
	public List<ContaCorrente> listarAtivas() {
		return contaRepository.findByAtivaTrue();
	}

	@Transactional(readOnly = true)
	public List<MovimentacaoContaCorrente> extrato(Integer contaId) {
		return movimentacaoRepository.findByContaIdOrderByDataHoraDesc(contaId);
	}

	@Transactional(readOnly = true)
	public BigDecimal saldo(Integer contaId) {
		return buscarPorId(contaId).getSaldoAtual();
	}
	
	
}
