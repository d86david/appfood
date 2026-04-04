package com.dsys.appfood.exception;

public class CategoriaJaCadastradaException extends EntidadeJaCadastradaException{
	
private static final long serialVersionUID = 1L;
	
	public CategoriaJaCadastradaException(String categoria) {
		super("A categoria " + categoria + " já está cadastrado");
	}
	
	public CategoriaJaCadastradaException(String categoria, Integer id) {
		super("Já existe a categoria " + categoria + "cadastrada ID " + id);
	}

}
