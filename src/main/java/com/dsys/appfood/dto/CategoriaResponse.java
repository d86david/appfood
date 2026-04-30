package com.dsys.appfood.dto;

import com.dsys.appfood.domain.model.Categoria;

public record CategoriaResponse(
		
		Integer id,
		String nome,
		boolean personalizavel
		
		) {
	public static CategoriaResponse from (Categoria categoria) {
		return new CategoriaResponse(
				
				categoria.getId(),
				categoria.getNome(),
				categoria.isPersonalizavel()
				
				);
	}
}
