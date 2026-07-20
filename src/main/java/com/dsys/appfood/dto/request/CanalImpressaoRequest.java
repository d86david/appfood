package com.dsys.appfood.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CanalImpressaoRequest(
		
	    @NotBlank(message = "O nome do canal é obrigatório")
	    @Size(max = 50, message = "O nome deve ter no máximo 50 caracteres")
	    String nome,
	    
	    @Size(max = 200)
	    String descricao
		
		) {}
