package com.dsys.appfood.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record AbrirCaixaRequest (
		@NotNull(message = "O operador é obrigatório para abrir o caixa")
		Integer operadorId,
		
		@NotNull(message = "O login do gerent é obrigatório")
		String loginGerente,
		
		@NotNull(message = "A senha do gerente é obrigatória")
		String senhaGerente,
		
		@PositiveOrZero(message = "O valor inicial não pode ser negativo")
		BigDecimal valorInicial
		
		){}
