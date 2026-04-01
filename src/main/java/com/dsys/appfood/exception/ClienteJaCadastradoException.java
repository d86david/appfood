package com.dsys.appfood.exception;

/**
 *  EXCEÇÃO - para Cliente já cadastrado e ativo
 * 
 */
public class ClienteJaCadastradoException extends RuntimeException{


	private static final long serialVersionUID = 1L;
	
	public ClienteJaCadastradoException(String telefone) {
		super("Ja existe um cliente ativo com o telefone: " + telefone);
	}
	

}
