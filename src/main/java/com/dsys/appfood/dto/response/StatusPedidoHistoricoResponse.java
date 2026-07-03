package com.dsys.appfood.dto.response;

import java.time.LocalDateTime;

import com.dsys.appfood.domain.enums.StatusPedido;
import com.dsys.appfood.domain.model.StatusPedidoHistorico;

public record StatusPedidoHistoricoResponse(
		
		Integer id,
		StatusPedido status,
		LocalDateTime dataHora,
		String usuarioNome
		) {
	public static StatusPedidoHistoricoResponse from(StatusPedidoHistorico historico) {
		return new StatusPedidoHistoricoResponse (
				historico.getId(),
				historico.getStatus(),
				historico.getDataHora(),
				historico.getUsuario() != null ? historico.getUsuario().getNome() : null
				);
	}
}
