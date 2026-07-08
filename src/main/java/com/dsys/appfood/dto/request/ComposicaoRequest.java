package com.dsys.appfood.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;

public record ComposicaoRequest(

		@NotEmpty(message ="Informe pelo menos um ingrediente")
		List<Integer> ingredientesIds

		) {}
