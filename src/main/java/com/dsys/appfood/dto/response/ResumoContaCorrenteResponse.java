package com.dsys.appfood.dto.response;

import java.math.BigDecimal;

/**
 * DTO interno para transporte dos dados de resumo
 */
public record ResumoContaCorrenteResponse (

	Integer contaId,
	BigDecimal totalEntradas,
	BigDecimal totalSaidas,
	BigDecimal saldoLiquido
	){}
