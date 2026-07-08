package com.dsys.appfood.dto.response;

import java.math.BigDecimal;

public record CustomizacaoResponse(

		Integer id,
		String tipo,
		String nome, // nome do ingrediente ou borda
		BigDecimal valor

		) {}
