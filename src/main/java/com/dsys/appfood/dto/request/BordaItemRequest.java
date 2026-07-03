package com.dsys.appfood.dto.request;

import jakarta.validation.constraints.NotNull;

public record BordaItemRequest(
		
		@NotNull(message = "O ID do Item é obrigatório")
		Integer itemId,
		
		@NotNull(message = "O ID da borda é obrigatório")
		Integer bordaId
		
		) {}
