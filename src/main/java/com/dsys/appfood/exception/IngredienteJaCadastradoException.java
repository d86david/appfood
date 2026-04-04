package com.dsys.appfood.exception;

public class IngredienteJaCadastradoException extends EntidadeJaCadastradaException {
	
private static final long serialVersionUID = 1L;
	
	public IngredienteJaCadastradoException(String ingrediente) {
		super("O Ingrediente " + ingrediente + " já está cadastrado");
	}
	
	public IngredienteJaCadastradoException(String ingrediente, Integer id) {
		super("Já existe o ingrediente " + ingrediente + "cadastrado ID " + id);
	}
	

}
