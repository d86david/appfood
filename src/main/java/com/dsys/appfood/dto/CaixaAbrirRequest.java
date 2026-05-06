package com.dsys.appfood.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record CaixaAbrirRequest (
		@NotNull(message = "O operador é obrigatório para abrir o caixa")
		Integer operadorId,
		
		@NotBlank(message = "O login do gerente é obrigatório")
		String loginGerente,
		
		@NotBlank(message = "A senha do gerente é obrigatória")
		String senhaGerente,
		
		@PositiveOrZero(message = "O valor inicial não pode ser negativo")
		BigDecimal valorInicial
		
		){}
