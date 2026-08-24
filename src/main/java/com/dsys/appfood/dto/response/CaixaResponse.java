package com.dsys.appfood.dto.response;

import com.dsys.appfood.domain.model.Caixa;

public record CaixaResponse(
		
		Integer id,
		String nome,
		String descricao,
		String localizacao,
		Boolean ativo
		
		) {
	
	public static CaixaResponse from (Caixa caixa) {
		return new CaixaResponse(
				caixa.getId(), 
				caixa.getNome(), 
				caixa.getDescricao(), 
				caixa.getLocalizacao(), 
				caixa.isAtivo()
				);
	}
}
