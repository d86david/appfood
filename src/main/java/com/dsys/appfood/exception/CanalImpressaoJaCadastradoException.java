package com.dsys.appfood.exception;

public class CanalImpressaoJaCadastradoException extends EntidadeJaCadastradaException{
	
	private static final long serialVersionUID = 1L;

	public CanalImpressaoJaCadastradoException(String nome, Integer id) {
		super("Já existe um canal com o nome: " + nome +" ID: " + id);
	}

	public CanalImpressaoJaCadastradoException(String nome) {
		super("Já existe um canal com o nome: " + nome);
	}

}
