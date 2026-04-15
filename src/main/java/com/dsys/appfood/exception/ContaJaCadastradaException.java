package com.dsys.appfood.exception;

public class ContaJaCadastradaException extends EntidadeJaCadastradaException {
	
private static final long serialVersionUID = 1L;
	
	public ContaJaCadastradaException(String nome) {
		super("Já existe uma conta cadastrada com o nome " + nome);
	}
	
	public ContaJaCadastradaException(String nome, Integer id) {
		super("Já existe a conta " + nome + "cadastrada ID " + id);
	}
	

}
