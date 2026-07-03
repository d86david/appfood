package com.dsys.appfood.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CancelarPedidoRequest(
		
		@NotNull(message = "O ID do gerente é obrigatório")
		Integer gerenteId,
		
		@NotBlank(message = "O motivo do cancelamento é obrigatório")
		String motivo
		
		) {}
