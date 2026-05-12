package com.dsys.appfood.dto.response;

import java.math.BigDecimal;



/**
 * DTO para resposta de pagamento em dinheiro com troco
 */
public record PagamentoComTrocoResponse (

	PagamentoResponse pagamento,
	BigDecimal troco,
	BigDecimal valorRecebido
){}