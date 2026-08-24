package com.dsys.appfood.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SessaoCaixaEstornoRequest(

		@NotNull(message = "O ID da sessão de caixa é obrigatório")
	    Integer sessaoCaixaId,
	    
	    @NotNull(message = "O ID da movimentação a ser estornado é obrigatório")
		Integer movimentacaoCaixaId,

		@NotBlank(message = "O login do gerente é obrigatório")
		String loginGerente,

		@NotBlank(message = "A senha do gerente é obrigatória")
		String senhaGerente,

		@NotBlank(message = "O motivo do estorno é obrigatório")
		String motivo
) {}
