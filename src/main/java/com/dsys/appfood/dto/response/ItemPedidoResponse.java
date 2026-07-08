package com.dsys.appfood.dto.response;

import java.math.BigDecimal;
import java.util.List;

import com.dsys.appfood.domain.model.ItemPedido;

public record ItemPedidoResponse(

		Integer id,
		Integer tamanhoId,
		String tamanhoNome,
		BigDecimal precoFinal,
		List<SubItemSaborResponse> sabores,
		List<CustomizacaoResponse> customizacoesGlobais

		) {

	public static ItemPedidoResponse from(ItemPedido item) {
		return new ItemPedidoResponse (
				item.getId(),
				item.getTamanho().getId(),
				item.getTamanho().getNome(),
				item.calcularPrecoFinal(),
				item.getSubItens().stream().map(SubItemSaborResponse::from).toList(),
				item.getCustomizacoesGlobais().stream()
						.map(c -> new CustomizacaoResponse(
								c.getId(),
								"BORDA",
								c.getBorda() != null ? c.getBorda().getNome() : null,
								c.getValorCobrado()
								))
				.toList()
				);
	}

}
