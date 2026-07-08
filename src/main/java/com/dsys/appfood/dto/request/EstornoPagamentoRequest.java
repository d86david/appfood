package com.dsys.appfood.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EstornoPagamentoRequest(

		@NotNull(message = "O ID do gerente é obrigatório" )
		Integer gerenteId,

		@NotBlank(message = "O motivo do estorno é obrigatório")
		String motivo
		) {}
