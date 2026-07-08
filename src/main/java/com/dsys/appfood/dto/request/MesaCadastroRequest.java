package com.dsys.appfood.dto.request;

import jakarta.validation.constraints.NotNull;

public record MesaCadastroRequest(

		@NotNull(message = "O numero da mesa é obrigatório")
		Integer numero,

		Integer capacidade

		) {}
