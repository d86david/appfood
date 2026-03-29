package com.dsys.appfood.exception;

/**
 *  EXCEÇÃO ESPECÍFICA - Herda da base (EntidadeNaoEncontradaException)
 *  
 *  Só chama super() com a mensagem certa
 */
public class TamanhoNaoEncontradoException extends EntidadeNaoEncontradaException{
	
	private static final long serialVersionUID = 1L;

	public TamanhoNaoEncontradoException(Integer id) {
		super("Tamanho não encontrado: id " + id,id);
	}

}
