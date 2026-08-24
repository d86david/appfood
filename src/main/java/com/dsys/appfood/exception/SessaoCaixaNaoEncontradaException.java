package com.dsys.appfood.exception;

/**
 *  EXCEÇÃO ESPECÍFICA - Herda da base (EntidadeNaoEncontradaException)
 *
 *  Só chama super() com a mensagem certa
 */
public class SessaoCaixaNaoEncontradaException extends EntidadeNaoEncontradaException {
	
	private static final long serialVersionUID = 1L;
	
	public SessaoCaixaNaoEncontradaException (Integer id) {
		super("Sessao não encontrada: id " + id, id);
	}

}
