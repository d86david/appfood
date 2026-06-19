package com.dsys.appfood.dto.request;

import jakarta.validation.constraints.NotNull;

public record MesaOcupacaoRequest(
		
		@NotNull
		Boolean ocupada
		
		) {}
