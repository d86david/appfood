package com.dsys.appfood.dto.request;

import jakarta.validation.constraints.NotBlank;

public record EnderecoRequest(
		
		@NotBlank(message = "O logradouro é obrigatório")
		String logradouro,
		
		@NotBlank(message = "O numero é obrigatório")
		String numero,
		
		String complemento,
		
		@NotBlank(message = "O bairro é obrigatório")
		String bairro, 
		
		@NotBlank (message = "A cidade é obrogatória")
		String cidade, 
		
		@NotBlank(message = "O estado é obrigatório")
		String uf,
		
		@NotBlank(message = "O cep é obrigatório")
		String cep,
		
		String pontoReferencia
		) {}
