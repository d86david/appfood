package com.dsys.appfood.dto.response;

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
		String operador,
		String caixaStatus // "ABERTO" ou "FECHADO" (só para informação)
		) {
	public static PagamentoResponse from(Pagamento p) {
		return new PagamentoResponse(
				p.getId(),
				p.getFormaPagamento(),
				p.getValor(),
				p.getOperador() != null ?  p.getOperador().getNome() : null,
				p.getCaixa() != null ? p.getCaixa().getStatus().toString() : null
				);
	}
}
