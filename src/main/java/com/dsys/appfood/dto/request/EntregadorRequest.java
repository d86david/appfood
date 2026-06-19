package com.dsys.appfood.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

public record EntregadorRequest(
		
		@NotBlank
		String nome,
		
		@NotBlank
		String telefone,
		
		@PositiveOrZero(message = "O valor da diária não pode ser negativo")
		BigDecimal valorDiaria,
		
		@PositiveOrZero(message = "O valor por entrega não pode ser negativo")
		BigDecimal valorPorEntrega
		
		) {}
