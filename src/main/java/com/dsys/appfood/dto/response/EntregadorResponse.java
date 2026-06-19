package com.dsys.appfood.dto.response;

import java.math.BigDecimal;

import com.dsys.appfood.domain.model.Entregador;

public record EntregadorResponse(
		
		Integer id,
		String nome,
		String telefone,
		Boolean ativo,
		BigDecimal valorDiaria,
		BigDecimal valorPorEntrega
		
		) {
	
	public static EntregadorResponse from (Entregador entregador) {
		return new EntregadorResponse(
				
				entregador.getId(),
				entregador.getNome(),
				entregador.getTelefone(),
				entregador.isAtivo(),
				entregador.getValorDiaria(),
				entregador.getValorPorEntrega()
				);
	}

}
