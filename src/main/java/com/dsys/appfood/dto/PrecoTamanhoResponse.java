package com.dsys.appfood.dto;

import java.math.BigDecimal;

public record PrecoTamanhoResponse(
		Integer tamanhoId,
		String tamanhoNome,
		BigDecimal valor
		) {

}
