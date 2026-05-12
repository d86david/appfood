package com.dsys.appfood.dto.response;

import java.math.BigDecimal;

public record PrecoTamanhoResponse(
		Integer tamanhoId,
		String tamanhoNome,
		BigDecimal valor
		) {

}
