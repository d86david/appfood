package com.dsys.appfood.exception;

/**
 *  EXCEÇÃO ESPECÍFICA - Herda da base (EntidadeNaoEncontradaException)
 *  
 *  Só chama super() com a mensagem certa
 */
public class PagamentoNaoEncontradoException extends EntidadeNaoEncontradaException{

	private static final long serialVersionUID = 1L;

	public PagamentoNaoEncontradoException(Integer id) {
		super("Pagamento não encontrado: id "+ id,id);
	}
}

