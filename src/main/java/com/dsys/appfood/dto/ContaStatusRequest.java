package com.dsys.appfood.dto;

import jakarta.validation.constraints.NotNull;

public record ContaStatusRequest(
		@NotNull
		Boolean ativo
		) {}
