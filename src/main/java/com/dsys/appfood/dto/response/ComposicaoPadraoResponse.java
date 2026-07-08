package com.dsys.appfood.dto.response;

import java.util.List;

import com.dsys.appfood.domain.model.ComposicaoPadrao;

public record ComposicaoPadraoResponse(

		Integer id,
		Integer prodtuoId,
		List<IngredienteResponse> ingredientes
		) {
	public static ComposicaoPadraoResponse from(ComposicaoPadrao c ) {
		List<IngredienteResponse> list = c.getIngredientes().stream()
				.map(IngredienteResponse :: from)
				.toList();
		return new ComposicaoPadraoResponse(c.getId(), c.getProduto().getId(), list);
	}

}
