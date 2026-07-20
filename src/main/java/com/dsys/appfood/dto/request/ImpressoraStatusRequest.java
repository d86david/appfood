package com.dsys.appfood.dto.request;

import jakarta.validation.constraints.NotNull;

public record ImpressoraStatusRequest(
		
		@NotNull
		 Boolean ativo
		
		) {}
