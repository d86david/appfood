package com.dsys.appfood.dto.response;

import java.util.Map;

public record ImpressaoResponse(
		
		 Map<String, String> conteudoCozinha, // chave = impressora, valor = conteúdo
	        String conteudoBalcao
		
		) {}
