package com.dsys.appfood.exception;

public class MesaJaCadastradaException extends EntidadeJaCadastradaException {
	
private static final long serialVersionUID = 1L;
	
	public MesaJaCadastradaException(Integer numero) {
		super("A mesa " + numero + " já está cadastrado");
	}
	
	public MesaJaCadastradaException(Integer numero, Integer id) {
		super("Já existe a mesa" + numero + "cadastrada ID " + id);
	}
	

}
