package com.dsys.appfood.exception;

/**
 * EXCEÇÃO BASE - Herdar dela todas "não encontrado"       
 *         												   
 * O GlobalHandler pode capturar todas as "não encontrado" 
 * com um único @ExceptionHandler(EntidadeNãoEncontrada)   
 * e retornar HTTP 400 para todas elas de uma vez.         
 *  
 */
public class EntidadeNaoEncontradaException extends RuntimeException{
	
	//Guarda o ID que não foi encontrado - útil para logs e respostas
	private static final long serialVersionUID = 1L;
	private final Integer id;
	
	public EntidadeNaoEncontradaException(String mensagem, Integer id) {
		super(mensagem);
		this.id = id;
	}
	
	//Contrutor simples - sem ID (quando não tem)
	public EntidadeNaoEncontradaException(String mensagem) {
		super(mensagem);
		this.id = null;
	}
	
	public Integer getId() {
		return id;
	}

}
