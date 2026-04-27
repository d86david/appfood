package com.dsys.appfood.dto;

import java.math.BigDecimal;

/**
 * DTO interno para transporte dos dados de resumo
 */
public record ResumoCaixaResponse (

	Integer caixaId,
	BigDecimal totalEntradas,
	BigDecimal totalSaidas,
	BigDecimal saldoLiquido
	
	){}