package com.dsys.appfood.dto.request;

import com.dsys.appfood.domain.enums.TipoDocumento;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ClienteRequest(

		@NotBlank(message = "O nome do Cliente é obrgatório")
		String nome,

		@NotBlank(message = "O telefone principal é obrigatório" )
		String telefonePrincipal,

		String telefoneSegundario,

		@NotNull(message= "O tipo de documento é obrigatório")
		TipoDocumento tipoDocumento,

		String documento, // pode ser opcional dentro da regra

		@Email(message = "E-mail inválido")
		String email,

		String observacaoCliente,

		@NotNull(message = "O ID do endereço é obrigatório")
		Integer enderecoId

		) {}
