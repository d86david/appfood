package com.dsys.appfood.dto.response;

import java.math.BigDecimal;

import com.dsys.appfood.domain.model.Pagamento;

/**
 * DTO para resposta de pagamento em dinheiro com troco
 */
public record PagamentoComTrocoResponse(

		PagamentoResponse pagamento,
		BigDecimal troco,
		BigDecimal valorRecebido) {

	/**
	 * Método de fábrica para criar o DTO a partir dos dados da operação.
	 *
	 * @param pagamento     O pagamento que foi registrado (entidade JPA).
	 * @param troco         O troco calculado para devolver ao cliente.
	 * @param valorRecebido Quanto o cliente entregou em dinheiro.
	 * @return DTO completo com todos os dados.
	 */
	public static PagamentoComTrocoResponse of(Pagamento pagamento, BigDecimal troco, BigDecimal valorRecebido) {
		return new PagamentoComTrocoResponse(
				PagamentoResponse.from(pagamento),
				troco,
				valorRecebido
				);
	}
}