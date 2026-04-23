package com.dsys.appfood.dto;

import java.math.BigDecimal;

public class CaixaStatusResponse {

	private boolean aberto;
	private String operador;
	private BigDecimal saldoAtual;

	// CONSTRUTORES 
	public CaixaStatusResponse() {
		
	}
	
	public CaixaStatusResponse(boolean aberto, String operador, BigDecimal saldoAtual) {
		this.aberto = aberto;
		this.operador = operador;
		this.saldoAtual = saldoAtual;
	}
	
	
	// GETTERS

	public boolean isAberto() {
		return aberto;
	}

	public String getOperador() {
		return operador;
	}

	public BigDecimal getSaldoAtual() {
		return saldoAtual;
	}

}
