package com.dsys.appfood.dto;

import java.math.BigDecimal;

/**
 * DTO interno para transporte dos dados de resumo
 */
public class ResumoContaCorrenteResponse {

	private final Integer contaId;
	private final BigDecimal totalEntradas;
	private final BigDecimal totalSaidas;
	private final BigDecimal saldoLiquido;

	public ResumoContaCorrenteResponse(Integer contaId, BigDecimal totalEntradas, BigDecimal totalSaidas, BigDecimal saldoLiquido) {
		this.contaId = contaId;
		this.totalEntradas = totalEntradas;
		this.totalSaidas = totalSaidas;
		this.saldoLiquido = saldoLiquido;
	}
	
	// GETTERS

	public Integer getContaId() {
		return contaId;
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
