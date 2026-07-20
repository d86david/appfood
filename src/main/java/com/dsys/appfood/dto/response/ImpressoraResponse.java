package com.dsys.appfood.dto.response;

import com.dsys.appfood.domain.model.Impressora;

public record ImpressoraResponse(
		
	    Integer id,
	    String nome,
	    Integer larguraColunas,
	    String modelo,
	    String portaComunicacao,
	    Boolean ativa
	) {
	    public static ImpressoraResponse from(Impressora impressora) {
	        return new ImpressoraResponse(
	            impressora.getId(),
	            impressora.getNome(),
	            impressora.getLarguraColunas(),
	            impressora.getModelo(),
	            impressora.getPortaComunicacao(),
	            impressora.isAtiva()
	        );
	    }

}
