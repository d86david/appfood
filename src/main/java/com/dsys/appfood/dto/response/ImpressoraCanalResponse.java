package com.dsys.appfood.dto.response;

import com.dsys.appfood.domain.model.ImpressoraCanal;

public record ImpressoraCanalResponse(
		
		Integer id,
		String impresoraNome,
		String canalNome,
		Boolean ativo
		) {
	
	public static ImpressoraCanalResponse from(ImpressoraCanal mapeamento) {
		
		return new ImpressoraCanalResponse(
				
				mapeamento.getId(),
				mapeamento.getImpressora().getNome(),
				mapeamento.getCanal().getNome(),
				mapeamento.getAtivo()
				);
	}
}
