package com.dsys.appfood.dto;

import java.math.BigDecimal;

import com.dsys.appfood.domain.enums.FormaPagamento;

/**
 * DTO para receber múltiplo pagamentos de uma vez
 * 
 * - Objetivo simples, sem lógica de negócio - Apenas transporta dados entre
 * camadas - Evita expor entidades diretamente
 */
public class PagamentoRequest {

	private FormaPagamento formaPagamento;
	private BigDecimal valor;

	// CONSTRUTORES

	public PagamentoRequest() {

	}

	// GETTERS E SETTERS

	public FormaPagamento getFormaPagamento() {
		return formaPagamento;
	}

	public void setFormaPagamento(FormaPagamento formaPagamento) {
		this.formaPagamento = formaPagamento;
	}

	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

}
