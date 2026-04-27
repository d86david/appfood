package com.dsys.appfood.dto;

import jakarta.validation.constraints.NotBlank;

public record CaixaFecharRequest(
		
		@NotBlank(message = "O login do gerente é obrigatório")
		String loginGerente,
		
		@NotBlank(message = "A senha do gerente é obrigatoria")
		String senhaGerente
		
		) {

}
