package com.dsys.appfood.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record ProdutoRequest(
		
		@NotBlank(message = "O nome do produto é obrigatório")
		String nome,
		
		boolean imprimeCozinha,
		
		@NotNull(message = "A categoria é obrigatória")
		Integer categoriaId,
		
		@NotEmpty(message = "Pelo menos um preço deve ser informado")
		List<PrecoTamanhoRequest> precos
		
		) {

}
