package com.dsys.appfood.dto.request;

import com.dsys.appfood.domain.enums.TipoCustomizacao;

import jakarta.validation.constraints.NotNull;

public record CustomizacaoRequest(

		@NotNull(message = "O ID do item é obrigatório")
		Integer itemId,

		@NotNull(message = "O ID do sub-item (sabor) é obrigatório")
		Integer subItemId,

		@NotNull(message = "O ID do ingrediente é obrigatório")
		Integer ingredienteId,

		@NotNull(message = "O tipo de customização é obrigatório")
		TipoCustomizacao tipo

		) {}
