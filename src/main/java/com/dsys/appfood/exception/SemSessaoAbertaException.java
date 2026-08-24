package com.dsys.appfood.exception;

/**
 *  EXCEÇÃO DE ESTADO - para caixa sem Sessao aberta
 *
 */
public class SemSessaoAbertaException extends NegocioException{

	private static final long serialVersionUID = 1L;

	public SemSessaoAbertaException(String nome) {
		super("O Caixa " + nome + " não tem uma sessão aberta. Abra-a antes de oprerar.");
	}

}
