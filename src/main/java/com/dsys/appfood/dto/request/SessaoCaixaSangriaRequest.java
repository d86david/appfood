package com.dsys.appfood.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record SessaoCaixaSangriaRequest(

		@NotNull(message = "O ID da sessão de caixa é obrigatório")
		Integer sessaoCaixaId,
		
		@NotBlank(message = "O login do gerente é obrigatório")
		String loginGerente,

		@NotBlank(message = "A senha do gerente é obrigatória")
		String senhaGerente,

		@PositiveOrZero(message = "O valor da sangria não pode ser negativo")
		BigDecimal valor,

		@NotBlank(message = "O motivo da sangria é obrigatório")
		String motivo

		) {}
