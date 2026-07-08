package com.dsys.appfood.dto.request;

import com.dsys.appfood.domain.enums.TipoPedido;

import jakarta.validation.constraints.NotNull;

public record PedidoRequest(

		Integer clienteId, // Opcional: Se não informado, será balcão anônimo

		@NotNull(message = "O tipo do pedido é obrigatório")
		TipoPedido tipo,

		String nomeBalcao, // Obrigatório se clienteId for nulo e tipo = BALCAO

		Integer numeroMesa, // Obrigatório se tipo = MESA

		String obsPedido

		) {}
