package com.dsys.appfood.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record SessaoCaixaFecharRequest(

		@NotNull(message = "O ID da sessão de caixa é obrigatório")
		Integer sessaoCaixaId,
		
		@NotBlank(message = "O login do gerente é obrigatório")
		String loginGerente,

		@NotBlank(message = "A senha do gerente é obrigatoria")
		String senhaGerente,
		
		@PositiveOrZero(message = "O valor final não pode ser negativo")
		BigDecimal valorFinal

		) {}
