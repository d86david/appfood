package com.dsys.appfood.dto.request;

import jakarta.validation.constraints.NotNull;

public record AdicionarItemRequest(

		@NotNull(message = " O ID do tamanho é obrigatório")
		Integer tamanhoId,

		/**
		 * CAMPO OPCIONAL:  Se for um produto simples (bebida, sobremesa, etc), informe o ID do produto aqui.
		 * Se for Pizza, deixe null e use o endpoint /sabores para adicionar sabores.
		 */
		Integer produtoId

		) {}
