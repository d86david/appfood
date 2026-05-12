package com.dsys.appfood.dto.response;

import com.dsys.appfood.domain.model.Tamanho;

public record TamanhoResponse(
		
		Integer id,
		String nome
		
		) {
	
	// Factory Method facilita muito a conversão da Model para o DTO dentro da Service ou Controller.
	public static TamanhoResponse from(Tamanho tamanho) {
		return new TamanhoResponse(
				
				tamanho.getId(),
				tamanho.getNome()
				
				);
	}

}
