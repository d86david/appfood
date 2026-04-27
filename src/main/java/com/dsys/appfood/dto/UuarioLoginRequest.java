package com.dsys.appfood.dto;

import jakarta.validation.constraints.NotBlank;

public record UuarioLoginRequest(
		
		@NotBlank(message = "Login não pode ser vazio")
		String login,
		
		@NotBlank(message = "Senha não pode ser vazia")
		String senha
		
		) {}
