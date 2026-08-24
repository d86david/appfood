package com.dsys.appfood.exception;

public class CaixaJaCadastradoException extends EntidadeJaCadastradaException{
	
	private static final long serialVersionUID = 1L;
	
	public CaixaJaCadastradoException(String nome) {
		super("Caixa " + nome + " já está cadastrado");
	}

	public CaixaJaCadastradoException(String nome, Integer id) {
		super("Já existe um caixa " + nome + "cadastrado ID " + id);
	}

}
