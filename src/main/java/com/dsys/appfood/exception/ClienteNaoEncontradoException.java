package com.dsys.appfood.exception;

/**
 *  EXCEÇÃO ESPECÍFICA - Herda da base (EntidadeNaoEncontradaException)
 *  
 *  Só chama super() com a mensagem certa
 */
public class ClienteNaoEncontradoException extends EntidadeNaoEncontradaException{
	
	private static final long serialVersionUID = 1L;

	public ClienteNaoEncontradoException(Integer id) {
		super("Cliente não encontrado: id "+ id,id);
	}
	
	public ClienteNaoEncontradoException(String telefone) {
		super("Nenhum Cliente encontrado com o telefone: " + telefone);
	}

}
