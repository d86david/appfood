package com.dsys.appfood.dto;

import java.math.BigDecimal;

import com.dsys.appfood.domain.enums.FormaPagamento;
import com.dsys.appfood.domain.model.Pagamento;

/**
 * DTO para resposta de pagamento
 */
public record PagamentoResponse(
		
		Integer id,
		FormaPagamento formaPagto,
		BigDecimal valor,
		String operador
		) {
	public static PagamentoResponse from(Pagamento p) {
		return new PagamentoResponse(
				p.getId(),
				p.getFormaPagamento(),
				p.getValor(),
				p.getOperador().getNome()
				);
	}
}
