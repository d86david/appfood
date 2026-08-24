package com.dsys.appfood.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CaixaRequest(
		
		@NotBlank(message = "O nome do caixa é obrigatório")
		@Size(max = 50, message = "O nome deve ter o máximo 50 caracteres")
		String nome,
		
		@Size(max = 200)
		String descricao,
		
		@Size(max = 100)
		String localizacao
		
		) {}
