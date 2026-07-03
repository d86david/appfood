package com.dsys.appfood.dto.request;

import jakarta.validation.constraints.NotNull;

public record AdicionarSaborRequest(
		
		@NotNull(message = "O ID do item é obrigatório")
		Integer itemId,
		
		@NotNull(message = "O ID do produto é obrigatório")
		Integer produtoId
		
		) {}
