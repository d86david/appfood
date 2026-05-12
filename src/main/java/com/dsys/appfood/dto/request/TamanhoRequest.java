package com.dsys.appfood.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TamanhoRequest(
		
		@NotBlank(message = "O nome do tamanho é obrigatório")
		@Size(min =1, max = 50, message = "O nome do tamanho deve ter entre 1 e 50 caracteres")
		String nome
		
		) {}
