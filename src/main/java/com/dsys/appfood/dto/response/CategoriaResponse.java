package com.dsys.appfood.dto.response;

import com.dsys.appfood.domain.model.Categoria;

public record CategoriaResponse(
		
		Integer id,
		String nome,
		boolean personalizavel,
		String impressora
		
		) {
	public static CategoriaResponse from (Categoria categoria) {
		return new CategoriaResponse(
				
				categoria.getId(),
				categoria.getNome(),
				categoria.isPersonalizavel(),
				categoria.getImpressora()
				
				);
	}
}
