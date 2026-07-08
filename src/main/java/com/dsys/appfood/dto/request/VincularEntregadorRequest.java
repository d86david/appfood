package com.dsys.appfood.dto.request;

import jakarta.validation.constraints.NotNull;

public record VincularEntregadorRequest(

		@NotNull(message = "O ID do entregador é obrigatório")
		Integer entregadorId

		) {}
