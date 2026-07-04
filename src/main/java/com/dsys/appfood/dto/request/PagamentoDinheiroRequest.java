package com.dsys.appfood.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * DTO para pagamento em DINHEIRO com cálculo automático de troco.
 * 
 * @param valorRecebido Quanto o cliente entregou em dinheiro (ex: R$ 100,00)
 * @param operadorId    ID do operador que está registrando o pagamento
 */
public record PagamentoDinheiroRequest(
		
		@NotNull(message = "O valor recebido é obrigatório")
		@Positive(message = "O valor recebido deve ser maior que zero")
		BigDecimal valorRecebido,
		
		@NotNull(message = "O ID do operador é obrigatório")
		Integer operadorId
		) {}
