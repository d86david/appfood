package com.dsys.appfood.exception;

/**
 * EXCEÇÃO BASE - Herdar dela todas "já cadastrado"
 * 
 * O GlobalHandler pode capturar todas as "já cadastrado" com um
 * único @ExceptionHandler(EntidadeJaCadastradaEncontrada) e retornar HTTP 409
 * para todas elas de uma vez.
 * 
 */
public class EntidadeJaCadastradaException extends RuntimeException {

	// Guarda o ID que não foi encontrado - útil para logs e respostas
	private static final long serialVersionUID = 1L;
	private final Integer id;

	public EntidadeJaCadastradaException(String mensagem, Integer id) {
		super(mensagem);
		this.id = id;
	}

	// Contrutor simples - sem ID (quando não tem)
	public EntidadeJaCadastradaException(String mensagem) {
		super(mensagem);
		this.id = null;
	}

	public Integer getId() {
		return id;
	}

}
