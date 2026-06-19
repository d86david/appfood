package com.dsys.appfood.dto.response;

import com.dsys.appfood.domain.model.Mesa;

public record MesaResponse(
		
		Integer id, 
		Integer numero,
		Boolean ocupada,
		Integer capacidade,
		Boolean ativa
		
		) {
	
	public static MesaResponse from(Mesa mesa) {
		
		return new MesaResponse(
				mesa.getId(),
				mesa.getNumero(),
				mesa.isOcupada(),
				mesa.getCapacidade(),
				mesa.isAtiva()
				);
		
	}

}
