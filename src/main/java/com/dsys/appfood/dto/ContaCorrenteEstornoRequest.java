package com.dsys.appfood.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ContaCorrenteEstornoRequest(
		
		@NotNull(message = "O ID do gerente é obrigatório")
		Integer gerenteId,
		
		@NotBlank(message = "O motivo do estorno é obrigatório")
		String motivo
		
		) {}
