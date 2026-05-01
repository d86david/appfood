package com.dsys.appfood.exception;

public class BordaJaCadastradaException extends EntidadeJaCadastradaException {
	
private static final long serialVersionUID = 1L;
	
	public BordaJaCadastradaException(String borda) {
		super("A borda " + borda + " já está cadastrada");
	}
	
	public BordaJaCadastradaException(String borda, Integer id) {
		super("Já existe a borda " + borda + "cadastrada ID " + id);
	}
	

}
