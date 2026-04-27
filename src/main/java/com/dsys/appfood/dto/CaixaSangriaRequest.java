package com.dsys.appfood.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

public record CaixaSangriaRequest(
		
		@NotBlank(message = "O login do gerente é obrigatório")
		String loginGerente,
		
		@NotBlank(message = "A senha do gerente é obrigatória")
		String senhaGerente,
		
		@PositiveOrZero(message = "O valor da sangria não pode ser negativo")
		BigDecimal valor,
		
		@NotBlank(message = "O motivo da sangria é obrigatório")
		String motivo
		
		) {

}
