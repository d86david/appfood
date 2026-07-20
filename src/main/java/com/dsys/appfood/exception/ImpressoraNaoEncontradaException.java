package com.dsys.appfood.exception;

public class ImpressoraNaoEncontradaException extends EntidadeNaoEncontradaException{


	private static final long serialVersionUID = 1L;

	public ImpressoraNaoEncontradaException(Integer id) {
		super("Impressora não encontrada: id " + id, id);
	}

	public ImpressoraNaoEncontradaException(String nome) {
		super("Impressora '" + nome + "' não encontrada");
	}
	
	

}
