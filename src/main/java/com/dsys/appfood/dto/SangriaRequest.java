package com.dsys.appfood.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

public record SangriaRequest(
		
		// Redundante se recebermos via @PathVariable, 
		// mas podemos manter ou não. será mantido por clareza, mas no controller pegaremos o ID do path.
		Integer caxiaId, 
		
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
