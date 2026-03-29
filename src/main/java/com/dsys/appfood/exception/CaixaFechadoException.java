package com.dsys.appfood.exception;

/**
 *  EXCEÇÃO DE ESTADO - para caixa fechado
 * 
 */
public class CaixaFechadoException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	public CaixaFechadoException(Integer caixaId) {
		super("Caixa id " + caixaId + "está fechado. Abra-o antes de oprerar.");
	}
	
}
