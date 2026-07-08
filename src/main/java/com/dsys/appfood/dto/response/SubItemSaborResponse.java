package com.dsys.appfood.dto.response;

import java.math.BigDecimal;
import java.util.List;

import com.dsys.appfood.domain.model.SubItemSabor;

public record SubItemSaborResponse(

		Integer id,
		Integer produtoId,
		String produtoNome,
		BigDecimal precoSabor,
		List<CustomizacaoResponse> customizacoes
		) {
	public static SubItemSaborResponse from(SubItemSabor sub) {
		return new SubItemSaborResponse(
				sub.getId(),
				sub.getProduto().getId(),
				sub.getProduto().getNome(),
				sub.getPrecoSabor(),
				sub.getCustomizacoes().stream()
						.map(c -> new CustomizacaoResponse(
								c.getId(),
								c.getTipoCustomizacao().toString(),
								c.getIngrediente() != null ? c.getIngrediente().getNome() : null,
								c.getValorCobrado()
								))
						.toList()
				);
	}

}
