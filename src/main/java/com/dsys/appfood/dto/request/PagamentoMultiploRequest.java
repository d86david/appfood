package com.dsys.appfood.dto.request;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

/**
 * DTO para receber MÚLTIPLOS  pagamentos em uma única requisição
 * 
 * @param pagamentos Lista de PagamentoRequest (forma + valor)
 * @param operadorId ID do operador que está registrando os pagamentos
 */
public record PagamentoMultiploRequest(
		
		@NotEmpty(message = "Informe pelo menos um pagamento")
		@Valid // Valida cada item da lista
		List<PagamentoRequest> pagamentos,
		
		@NotNull(message = "O ID do operador é obrigatório")
		Integer operadorId
		
		) {}
