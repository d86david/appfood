package com.dsys.appfood.exception;

/**
 *  EXCEÇÃO ESPECÍFICA - Herda da base (EntidadeNaoEncontradaException)
 *  
 *  Só chama super() com a mensagem certa
 */
public class PrecoVariavelNaoEncontradoException extends EntidadeNaoEncontradaException{

	private static final long serialVersionUID = 1L;

	public PrecoVariavelNaoEncontradoException(Integer id) {
		super("Preço para o produto: id" + id + "não encontrado",id);
	}
}
