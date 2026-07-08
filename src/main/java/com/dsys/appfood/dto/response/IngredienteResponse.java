package com.dsys.appfood.dto.response;

import java.math.BigDecimal;

import com.dsys.appfood.domain.model.Ingrediente;

public record IngredienteResponse(

		Integer id,
		String nome,
		BigDecimal valorAdicional

		) {

	public static IngredienteResponse from(Ingrediente ingrediente) {

		return new IngredienteResponse(
				ingrediente.getId(),
				ingrediente.getNome(),
				ingrediente.getValorAdicional()
				);
	}

}
