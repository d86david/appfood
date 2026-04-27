package com.dsys.appfood.dto;

import com.dsys.appfood.domain.enums.TipoUsuario;
import com.dsys.appfood.domain.model.Usuario;

public record UsuarioResponse(
		
		Integer id,
	    String nome,
	    String login,
	    String telefone,
	    TipoUsuario tipo,
	    boolean ativo
		
		) {
	
	public static UsuarioResponse from (Usuario usuario) {
		return new UsuarioResponse(
				usuario.getId(),
				usuario.getNome(),
				usuario.getLogin(),
				usuario.getTelefone(),
				usuario.getTipo(),
				usuario.isAtivo()
				);
	}

}
