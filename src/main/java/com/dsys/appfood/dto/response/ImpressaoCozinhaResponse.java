package com.dsys.appfood.dto.response;

import java.util.Map;

public record ImpressaoCozinhaResponse(

		Map<String, String> conteudoCozinha // chave = impressora, valor = conteúdo

		) {}
