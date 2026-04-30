package com.dsys.appfood.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CategoriaRequest(
		
		@NotBlank(message = "O nome da categoria é obrigatório")
		String nome,
		
		@NotNull(message = "Defina se a categoria é personalizável (ex: Pizzas)")
		boolean personalizavel	
		){}
