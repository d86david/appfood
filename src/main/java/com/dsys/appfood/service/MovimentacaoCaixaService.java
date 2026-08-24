package com.dsys.appfood.service;

import com.dsys.appfood.repository.SessaoCaixaRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dsys.appfood.domain.enums.StatusCaixa;
import com.dsys.appfood.domain.model.MovimentacaoCaixa;
import com.dsys.appfood.domain.model.SessaoCaixa;
import com.dsys.appfood.dto.response.MovimentacaoCaixaResponse;
import com.dsys.appfood.dto.response.ResumoCaixaResponse;
import com.dsys.appfood.exception.CaixaNaoEncontradoException;
import com.dsys.appfood.exception.SemSessaoAbertaException;
import com.dsys.appfood.repository.CaixaRepository;
import com.dsys.appfood.repository.MovimentacaoCaixaRepository;

/**
 * Serviço responsavel por gerenciar as movimentaçãoes de caixa
 *
 * Responsabilidade ÚNICA: MovimentaçãoCaixa é uma entidade de "eventos" ou
 * "históricos" - Não deve ser alterada ou excluída após criada (imutabilidade
 * contábil). - portanto, este serviço NÃO oferece métodos de edição ou
 * exclusão.
 *
 * Este Service NÃO sabe nada sobre HTTP, Apenas processa e lança exceções de
 * negócio.
 */
@Service
public class MovimentacaoCaixaService {

	private final SessaoCaixaRepository sessaoCaixaRepository;
	private final CaixaRepository caixaRepository;
	private final MovimentacaoCaixaRepository movimentacaoRepository;

	public MovimentacaoCaixaService(MovimentacaoCaixaRepository movimentacaoRepository,
			CaixaRepository caixaRepository, SessaoCaixaRepository sessaoCaixaRepository) {
		this.movimentacaoRepository = movimentacaoRepository;
		this.caixaRepository = caixaRepository;
		this.sessaoCaixaRepository = sessaoCaixaRepository;
	}

	/**
	 * Consulta o extrato completo de um caixa específico
	 *
	 */
	@Transactional(readOnly = true)
	public List<MovimentacaoCaixa> extratoPorSessaoCaixa(SessaoCaixa sessao) {

		// Verifica se o caixa existe 
		if (!caixaRepository.existsById(sessao.getId())) {
			throw new CaixaNaoEncontradoException(sessao.getId());
		}
		
		// Verifica se o caixa tem sessão aberta antes de consultar movimentações
		if(!sessaoCaixaRepository.existsByCaixaAndStatus(sessao.getCaixa(), StatusCaixa.ABERTO)) {
			throw new SemSessaoAbertaException(sessao.getCaixa().getNome());
		}

		return movimentacaoRepository.findBySessaoCaixaOrderByDataHoraMovimentoDesc(sessao);
	}

	/**
	 * Consulta movimentação de um caixa, dentro de um período específico.
	 */
	@Transactional(readOnly = true)
	public List<MovimentacaoCaixa> extratoPorSessaoPeriodo(SessaoCaixa sessao, LocalDateTime inicio, LocalDateTime fim) {

		// Verifica se o caixa existe 
		if (!sessaoCaixaRepository.existsById(sessao.getId())) {
			throw new CaixaNaoEncontradoException(sessao.getId());
		}
		
		// Verifica se o caixa tem sessão aberta antes de consultar movimentações
		if(!sessaoCaixaRepository.existsByCaixaAndStatus(sessao.getCaixa(), StatusCaixa.ABERTO)) {
			throw new SemSessaoAbertaException(sessao.getCaixa().getNome());
		}

		if (inicio.isAfter(fim)) {
			throw new IllegalArgumentException("Data inicial não pode ser posterior à data final.");
		}

		return movimentacaoRepository.findBySessaoCaixaAndDataHoraMovimentoBetweenOrderByDataHoraMovimentoDesc(sessao,
				inicio, fim);
	}

	/**
	 * Calcula o total de entradas de um caixa (vendas e aportes)
	 */
	@Transactional(readOnly = true)
	public BigDecimal totalEntradas(Integer sessaoCaixaId) {

		BigDecimal totalEntradas = movimentacaoRepository.totalEntradas(sessaoCaixaId);

		// Se o banco não encontrar nada, o SUM retorna null.
		// É boa prática garantir que retorne ZERO nesses casos.
		return totalEntradas;
	}

	/**
	 * Calcula o total de saídas de um caixa (sangrias e fechamento)
	 */
	@Transactional(readOnly = true)
	public BigDecimal totalSaidas(Integer sessaoCaixaId) {

		BigDecimal totalSaidas = movimentacaoRepository.totalSaidas(sessaoCaixaId);

		// Se o banco não encontrar nada, o SUM retorna null.
		// É boa prática garantir que retorne ZERO nesses casos.
		return totalSaidas;

	}

	/**
	 * Calcula o saldo líquido do caixa com base nas movimentações. Fórmula: Total
	 * Entradas - Total Saídas
	 */
	@Transactional(readOnly = true)
	public BigDecimal saldoLiquido(Integer caixaId) {
		return totalEntradas(caixaId).subtract(totalSaidas(caixaId));
	}


	/**
	 * Gera um resumo financeiro do caixa por período.
	 */
	@Transactional(readOnly = true)
	public ResumoCaixaResponse gerarResumoCaixaPeriodo(Integer caixaId, LocalDateTime inicio, LocalDateTime fim) {
	    if (!caixaRepository.existsById(caixaId)) {
	        throw new CaixaNaoEncontradoException(caixaId);
	    }

	    BigDecimal entradas = movimentacaoRepository.totalEntradasPeriodo(caixaId, inicio, fim);
	    BigDecimal saidas = movimentacaoRepository.totalSaidasPeriodo(caixaId, inicio, fim);
	    BigDecimal saldo = entradas.subtract(saidas);

	    return new ResumoCaixaResponse(caixaId, entradas, saidas, saldo);
	}

	/**
	 * Gera um resumo financeiro do caixa do dia .
	 *
	 * Método utilitário para pegar o resumo apenas de hoje (00:00 até agora)
	 */
	@Transactional(readOnly = true)
	public ResumoCaixaResponse gerarResumoHoje(Integer caixaId) {
	    LocalDateTime inicio = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
	    LocalDateTime fim = LocalDateTime.now();
	    return gerarResumoCaixaPeriodo(caixaId, inicio, fim);
	}

	/**
	 * Gera um resumo financeiro sem especificar um período .
	 *
	 * Método utilitário para pegar o resumo
	 */
	@Transactional(readOnly = true)
	public ResumoCaixaResponse gerarResumoCaixa (Integer caixaId) {

		if(!caixaRepository.existsById(caixaId)) {
			throw new CaixaNaoEncontradoException(caixaId);
		}

		BigDecimal entradas = movimentacaoRepository.totalEntradas(caixaId);
		BigDecimal saidas = movimentacaoRepository.totalSaidas(caixaId);
		BigDecimal saldo = entradas.subtract(saidas);

		return new ResumoCaixaResponse(caixaId, entradas, saidas, saldo);
	}


	// =============================================================
	// MÉTODOS DTO (conversão dentro da transação)
	// =============================================================
	@Transactional(readOnly = true)
	public List<MovimentacaoCaixaResponse> listarMovimentacoesResponse(SessaoCaixa sessao,
			LocalDateTime inicio,LocalDateTime fim){

		List<MovimentacaoCaixa> movs;

		if(inicio != null && fim != null) {
			movs = extratoPorSessaoPeriodo(sessao, inicio, fim);
		}else {
			movs = extratoPorSessaoCaixa(sessao);
		}

		return movs.stream()
				.map(MovimentacaoCaixaResponse::from)
				.collect(Collectors.toList());

	}

}
