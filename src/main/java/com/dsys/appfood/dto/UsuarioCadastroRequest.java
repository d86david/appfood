package com.dsys.appfood.dto;

import com.dsys.appfood.domain.enums.TipoUsuario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UsuarioCadastroRequest(
		
		@NotBlank(message = "O nome é obrigatório")
		String nome,
		
		@NotBlank(message = "O login do é obrigatório")
		String login,
		
		@NotBlank(message = "A senha é obrigatória")
		@Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres")
		String senha, 
		
		String telefone,
		
		@NotNull(message = "O tipo de usuário é obrigatório")
		TipoUsuario tipo
		
		) {}
