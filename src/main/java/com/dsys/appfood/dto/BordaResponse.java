package com.dsys.appfood.dto;

import java.math.BigDecimal;

import com.dsys.appfood.domain.model.Borda;

public record BordaResponse(
		
		Integer id,
		String nome,
		BigDecimal valorAdicional
		
		) {
	public static BordaResponse from(Borda borda) {
		return new BordaResponse(
				borda.getId(),
				borda.getNome(),
				borda.getValorAdicional()
				);
	}
}
