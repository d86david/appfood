package com.dsys.appfood.exception;

/**
 *  EXCEÇÃO ESPECÍFICA - Herda da base (EntidadeNaoEncontradaException)
 *  
 *  Só chama super() com a mensagem certa
 */
public class IngredienteNaoEncontradoException extends EntidadeNaoEncontradaException{
 
	private static final long serialVersionUID = 1L;

	public IngredienteNaoEncontradoException(Integer id) {
		super("Ingrediente não encontrado: id " + id, id);
	}

}
