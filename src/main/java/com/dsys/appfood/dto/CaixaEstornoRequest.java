package com.dsys.appfood.dto;


import jakarta.validation.constraints.NotBlank;

public record CaixaEstornoRequest(

	
		@NotBlank(message = "O login do gerente é obrigatório")
		String loginGerente,
		
		@NotBlank(message = "A senha do gerente é obrigatória")
		String senhaGerente,
		
		@NotBlank(message = "O motivo do estorno é obrigatório")
		String motivo

) {}
