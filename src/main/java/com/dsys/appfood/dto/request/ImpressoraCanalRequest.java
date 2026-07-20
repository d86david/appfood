package com.dsys.appfood.dto.request;

import jakarta.validation.constraints.NotNull;

public record ImpressoraCanalRequest(
		
	    @NotNull(message = "O ID da impressora é obrigatório")
	    Integer impressoraId,
	    
	    @NotNull(message = "O ID do canal é obrigatório")
	    Integer canalId	
		) {}
