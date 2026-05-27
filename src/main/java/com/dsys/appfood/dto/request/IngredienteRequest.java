package com.dsys.appfood.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

public record IngredienteRequest(
		
		@NotBlank(message = "O nome do ingrediente é obrigatório")
		String nome,
		
		@PositiveOrZero(message = "O valor adicional não pode ser negativo")
		BigDecimal valorAdicional
		
		) {}
