package com.dsys.appfood.exception;

/**
 *  EXCEÇÃO para regras de negócios gerais.
 *  
 */
public class NegocioException extends RuntimeException{
	
	private static final long serialVersionUID = 1L;

	public NegocioException(String mensagem) {
		super(mensagem);
	}

}
