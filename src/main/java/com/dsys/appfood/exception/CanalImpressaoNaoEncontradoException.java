package com.dsys.appfood.exception;

public class CanalImpressaoNaoEncontradoException extends EntidadeNaoEncontradaException {

	private static final long serialVersionUID = 1L;
	
	public CanalImpressaoNaoEncontradoException(Integer id) {
		super("Canal não encontrado: id " + id, id);
	}
	
	public CanalImpressaoNaoEncontradoException(String nome) {
		super("Canal '" + nome + "' não encontrado");

	}
	
	

}
