package com.dsys.appfood.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UsuarioAlterarSenhaRequest(
		
		@NotBlank(message = "A senha é obrigatória")
		@Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres")
		String senhaAtual,
		
		@NotBlank(message = "A senha é obrigatória")
		@Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres")
		String senhaNova
		
		) {}
