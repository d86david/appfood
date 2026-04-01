package com.dsys.appfood.exception;

/**
 *  EXCEÇÃO ESPECÍFICA - Herda da base (EntidadeNaoEncontradaException)
 *  
 *  Só chama super() com a mensagem certa
 */
public class EnderecoNaoEncontradoException extends EntidadeNaoEncontradaException{
	
	private static final long serialVersionUID = 1L;

	public EnderecoNaoEncontradoException (Integer id) {
		super("Entregador não encontrado: id" + id, id);
	}

}
