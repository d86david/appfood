package com.dsys.appfood.dto;

import java.math.BigDecimal;

//=========================================================
// DTO INTERNO - Agrupa Tamanho + Valor para o Service
// Não vai para o banco, só transporta dados entre métodos
//=========================================================
public class PrecoTamanhoRequest {
	
	private Integer tamanhoId;
	private BigDecimal valor;
	
	//=========================================================
	// CONTRUTORES
	//=========================================================
	
	public PrecoTamanhoRequest() {
		
	}
	
	public PrecoTamanhoRequest(Integer tamanhoId, BigDecimal valor) {
		super();
		this.tamanhoId = tamanhoId;
		this.valor = valor;
	}
	
	//=========================================================
	// GETTERS
	//=========================================================
	
	public Integer getTamanhoId() {
		return tamanhoId;
	}
	
	public BigDecimal getValor() {
		return valor;
	}

}
