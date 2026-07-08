package com.dsys.appfood.dto.request;

import jakarta.validation.constraints.NotNull;

public record ReabrirPedidoRequest(

		@NotNull (message = "O ID do gerente é obrigatório")
		Integer gerenteId

		) {}
