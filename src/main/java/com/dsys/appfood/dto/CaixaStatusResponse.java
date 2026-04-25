package com.dsys.appfood.dto;

import java.math.BigDecimal;

import com.dsys.appfood.domain.model.Caixa;

public record CaixaStatusResponse(
		
		boolean aberto,
		String operador,
		BigDecimal saldoAtual	
		) {
	
	// Fábrica para quando o caixa está aberto
	public static CaixaStatusResponse deCaixaAberto(Caixa caixa) {
		return new CaixaStatusResponse(			
				true,
				caixa.getOperador() != null ? caixa.getOperador().getNome() : null,
				caixa.getSaldo()
				);
	}
	
	// Fábrica para quando não há caixa aberto
	
	public static CaixaStatusResponse vazio() {
		return new CaixaStatusResponse(false, null, BigDecimal.ZERO);
	}
	
}
