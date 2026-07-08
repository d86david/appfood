package com.dsys.appfood.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ProdutoSimplesRequest(

		@NotBlank(message = "O nome do produto é obrigatório")
		String nome,

		Boolean imprimeCozinha,

		@NotNull(message = "A Categoria éobrigatória")
		Integer categoriaId,

		@NotNull(message = "O valor do produto é obrigatório")
		@Positive(message = "O valor deve ser maior que zero")
		BigDecimal valor

		) {}
