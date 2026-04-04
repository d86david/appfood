package com.dsys.appfood.exception;

public class TamanhoJaCadastradoException extends EntidadeJaCadastradaException {
	
private static final long serialVersionUID = 1L;
	
	public TamanhoJaCadastradoException(String tamanho) {
		super("O tamanho " + tamanho + " já está cadastrado");
	}
	
	public TamanhoJaCadastradoException(String tamanho, Integer id) {
		super("Já existe o tamanho " + tamanho + "cadastrado ID " + id);
	}

}
