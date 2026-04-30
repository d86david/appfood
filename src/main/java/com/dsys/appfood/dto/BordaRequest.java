package com.dsys.appfood.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;

public record BordaRequest(
		
		@NotBlank(message = "O nome da borda é obrigatório")
		String nome,
		
		BigDecimal valorAdicional
		
		) {}
