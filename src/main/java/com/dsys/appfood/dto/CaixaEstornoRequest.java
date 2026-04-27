package com.dsys.appfood.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record CaixaEstornoRequest(

		@NotBlank(message = "O login do gerente é obrigatório")
		String loginGerente,
		
		@NotBlank(message = "A senha do gerente é obrigatória")
		String senhaGerente,
		
		@Positive(message = "O valor estornado não pode ser negativo")
		BigDecimal valor, 
		
		@NotBlank(message = "O motivo do estorno é obrigatório")
		String motivo

) {}
