package com.dsys.appfood.dto;

import com.dsys.appfood.domain.enums.TipoUsuario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UsuarioEdicaoRequest(
		@NotBlank(message = "O nome é obrigatório")
		String nome,
		
		String telefone,
		
		@NotNull(message = "O tipo de usuario é obrigatório")
		TipoUsuario tipo
		) {}
