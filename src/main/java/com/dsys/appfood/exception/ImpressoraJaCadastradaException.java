package com.dsys.appfood.exception;

public class ImpressoraJaCadastradaException extends EntidadeJaCadastradaException{

	private static final long serialVersionUID = 1L;

	public ImpressoraJaCadastradaException(String nome, Integer id) {
		super("Já existe uma impressora com o nome: " + nome +" ID: " + id);
	}

	public ImpressoraJaCadastradaException(String nome) {
		super("Já existe uma impressora com o nome: " + nome);
	}
	
	
}
