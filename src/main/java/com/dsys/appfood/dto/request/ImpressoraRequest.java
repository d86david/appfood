package com.dsys.appfood.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ImpressoraRequest(
		
		@NotBlank(message = "O nome da impressora é obrigatório")
		@Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
		String nome,
		
		@NotNull(message = "A largura em colunas é obrigatória")
		@Min(value = 20, message = "A largura mínima é 20 colunas")
		@Max(value = 100, message = "A largura máxima é 100 colunas")
		Integer larguraColunas,
		
		String modelo,
		
		String portaComunicacao
		
		) {}
