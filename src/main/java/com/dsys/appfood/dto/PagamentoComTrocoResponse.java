package com.dsys.appfood.dto;

import java.math.BigDecimal;

import com.dsys.appfood.domain.model.Pagamento;

/**
 * DTO para resposta de pagamento em dinheiro com troco
 */
public class PagamentoComTrocoResponse {

	private Pagamento pagamento;
	private BigDecimal troco;
	private BigDecimal valorRecebido;

	// CONSTRUTORES
	public PagamentoComTrocoResponse() {

	}

	// GETTERS E SETTERS
	public Pagamento getPagamento() {
		return pagamento;
	}

	public void setPagamento(Pagamento pagamento) {
		this.pagamento = pagamento;
	}

	public BigDecimal getTroco() {
		return troco;
	}

	public void setTroco(BigDecimal troco) {
		this.troco = troco;
	}

	public BigDecimal getValorRecebido() {
		return valorRecebido;
	}

	public void setValorRecebido(BigDecimal valorRecebido) {
		this.valorRecebido = valorRecebido;
	}

}
