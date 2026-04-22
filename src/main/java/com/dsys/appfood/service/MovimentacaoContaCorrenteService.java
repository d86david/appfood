package com.dsys.appfood.service;

import com.dsys.appfood.domain.model.MovimentacaoContaCorrente;
import com.dsys.appfood.dto.ResumoContaCorrenteResponse;
import com.dsys.appfood.exception.ContaNaoEncontradaException;
import com.dsys.appfood.repository.ContaCorrenteRepository;
import com.dsys.appfood.repository.MovimentacaoContaCorrenteRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Serviço responsavel por gerenciar as movimentaçãoes da Conta Corrente
 * 
 * Responsabilidade ÚNICA: MovimentaçãoContaCorrente é uma entidade de "eventos"
 * ou "históricos" - Não deve ser alterada ou excluída após criada
 * (imutabilidade contábil). - portanto, este serviço NÃO oferece métodos de
 * edição ou exclusão.
 * 
 * Este Service NÃO sabe nada sobre HTTP, Apenas processa e lança exceções de
 * negócio.
 */
@Service
public class MovimentacaoContaCorrenteService {

	private final ContaCorrenteRepository contaCorrenteRepository;
	private final MovimentacaoContaCorrenteRepository movimentacaoContaCorrenteRepository;

	public MovimentacaoContaCorrenteService(MovimentacaoContaCorrenteRepository movimentacaoContaCorrenteRepository,
			ContaCorrenteRepository contaCorrenteRepository) {
		this.movimentacaoContaCorrenteRepository = movimentacaoContaCorrenteRepository;
		this.contaCorrenteRepository = contaCorrenteRepository;
	}

	/**
	 * Consulta o extrato completo de um caixa específico
	 * 
	 */
	@Transactional(readOnly = true)
	public List<MovimentacaoContaCorrente> extratoPorConta(Integer contaId) {

		// Verifica se a conta exite antes de consultar a movimentação
		if (!contaCorrenteRepository.existsById(contaId)) {
			throw new ContaNaoEncontradaException(contaId);
		}

		return movimentacaoContaCorrenteRepository.findByContaIdOrderByDataHoraDesc(contaId);
	}

	/**
	 * Consulta mpvimentação de uma conta, dentro de um período específico
	 */
	@Transactional(readOnly = true)
	public List<MovimentacaoContaCorrente> extratoPorContaPeriodo(Integer contaId, LocalDateTime inicio,
			LocalDateTime fim) {

		// Verifica se a conta existe antes de consultar as movimentações
		if (!contaCorrenteRepository.existsById(contaId)) {
			throw new ContaNaoEncontradaException(contaId);
		}

		// Verifica se a data de início é posterior à data final
		if (inicio.isAfter(fim)) {
			throw new IllegalArgumentException("Data inicial não pode ser posterior à data final.");
		}

		return movimentacaoContaCorrenteRepository.findByContaIdAndDataHoraBetweenOrderByDataHoraDesc(contaId, inicio,
				fim);
	}

	/**
	 * Calcula o total de entradas de uma conta (vendas e aportes)
	 */
	@Transactional(readOnly = true)
	public BigDecimal totalEntradasConta(Integer contaId) {
		BigDecimal totalEntradasConta = movimentacaoContaCorrenteRepository.totalEntradasConta(contaId);

		return totalEntradasConta;
	}
	
	/**
	 * Calcula o total de saídas de uma conta 
	 */
	@Transactional(readOnly = true)
	public BigDecimal totalSaidasConta(Integer contaId) {

		BigDecimal totalSaidas = movimentacaoContaCorrenteRepository.totalSaidasConta(contaId);

		return totalSaidas;

	}
	
	/**
	 * Calcula o saldo líquido da conta com base nas movimentações. Fórmula: Total
	 * Entradas - Total Saídas
	 */
	@Transactional(readOnly = true)
	public BigDecimal saldoLiquido(Integer contaId) {
		return totalEntradasConta(contaId).subtract(totalSaidasConta(contaId));
	}
	
	/**
	 * Gera um resumo financeiro da conta por período.
	 */
	@Transactional(readOnly = true)
	public ResumoContaCorrenteResponse gerarResumoContaPeriodo(Integer contaId, LocalDateTime inicio, LocalDateTime fim) {
		
		if(!contaCorrenteRepository.existsById(contaId)) {
			throw new ContaNaoEncontradaException(contaId);
		}
		
		BigDecimal entradas = movimentacaoContaCorrenteRepository.totalEntradasPeriodo(contaId, inicio, fim);
		BigDecimal saidas = movimentacaoContaCorrenteRepository.totalSaidasPeriodo(contaId, inicio, fim);
		BigDecimal saldo = entradas.subtract(saidas);
		
		return new ResumoContaCorrenteResponse(contaId, entradas, saidas, saldo);
		
	}
	
	/**
	 * Gera um resumo financeiro da conta do dia .
	 * 
	 * Método utilitário para pegar o resumo apenas de hoje (00:00 até agora)
	 */
	@Transactional(readOnly = true)
	public ResumoContaCorrenteResponse gerarResumoHoje(Integer contaId) {
		
		LocalDateTime inicio = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
		LocalDateTime fim = LocalDateTime.now();
		
		return gerarResumoContaPeriodo(contaId, inicio, fim);
		
	}
	

}
