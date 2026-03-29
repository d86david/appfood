package com.dsys.appfood.exception;

import java.time.LocalDateTime;

/**
 * Objeto de resposta do Erro
 */
public class ErroResponse {
	
	private final String codigo;
	private final String mensagem;
	private final LocalDateTime timestamp = LocalDateTime.now();
	
	public ErroResponse(String codigo, String mensagem) {
		this.codigo = codigo;
		this.mensagem = mensagem;
	}

	public String getCodigo() {
		return codigo;
	}

	public String getMensagem() {
		return mensagem;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}
	
	

}
