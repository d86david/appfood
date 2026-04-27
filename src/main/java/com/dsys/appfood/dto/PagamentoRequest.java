package com.dsys.appfood.dto;

import java.math.BigDecimal;

import com.dsys.appfood.domain.enums.FormaPagamento;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * DTO para receber múltiplo pagamentos de uma vez
 * 
 * - Objetivo simples, sem lógica de negócio - Apenas transporta dados entre
 * camadas - Evita expor entidades diretamente
 */
public record PagamentoRequest (

	@NotNull(message = "A forma de pagamento é obrigatória")
	FormaPagamento formaPagamento,
	
	@NotNull(message = "O valor é obrigatório")
	@Positive(message = "O valor não pode ser negativo")
	BigDecimal valor
	) {}