package com.dsys.appfood.dto;

import jakarta.validation.constraints.NotNull;

public record UsuarioStatusRequest(
		
		 @NotNull 
		 Boolean ativo
		
		) {}
