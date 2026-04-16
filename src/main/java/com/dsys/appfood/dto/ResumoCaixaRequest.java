package com.dsys.appfood.dto;

import java.math.BigDecimal;

/**
 * DTO interno para transporte dos dados de resumo
 */
public class ResumoCaixaRequest {

	private final Integer caixaId;
	private final BigDecimal totalEntradas;
	private final BigDecimal totalSaidas;
	private final BigDecimal saldoLiquido;

	public ResumoCaixaRequest(Integer caixaId, BigDecimal totalEntradas, BigDecimal totalSaidas, BigDecimal saldoLiquido) {
		this.caixaId = caixaId;
		this.totalEntradas = totalEntradas;
		this.totalSaidas = totalSaidas;
		this.saldoLiquido = saldoLiquido;
	}

	// Getters
	public Integer getCaixaId() {
		return caixaId;
	}

	public BigDecimal getTotalEntradas() {
		return totalEntradas;
	}

	public BigDecimal getTotalSaidas() {
		return totalSaidas;
	}

	public BigDecimal getSaldoLiquido() {
		return saldoLiquido;
	}

}
