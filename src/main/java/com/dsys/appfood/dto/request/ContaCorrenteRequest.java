package com.dsys.appfood.dto.request;

import com.dsys.appfood.domain.enums.TipoConta;

import jakarta.validation.constraints.NotBlank;

public record ContaCorrenteRequest(

		@NotBlank
		String nome,

		@NotBlank
		TipoConta tipo,

		String codBanco,
		String banco,
		String agencia,
		String conta

		) {}
