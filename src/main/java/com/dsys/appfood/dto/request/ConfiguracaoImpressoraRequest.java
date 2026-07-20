package com.dsys.appfood.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ConfiguracaoImpressoraRequest(
		
		@NotBlank(message = "O nome da impressora é obrigatório")
		String nome,
		
		@NotNull(message = "A quantidade de colunas da impressora é obrigatória")
		Integer larguraColunas,
		
		@NotBlank(message = "O nome da impressora é obrigatório")
		String modelo,
		
		@NotBlank(message = "O nome da impressora é obrigatório")
		String impressoraTipo
		
		) {

}
