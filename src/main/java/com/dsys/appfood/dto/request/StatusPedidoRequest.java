package com.dsys.appfood.dto.request;

import com.dsys.appfood.domain.enums.StatusPedido;

import jakarta.validation.constraints.NotNull;

public record StatusPedidoRequest(
		
		@NotNull(message = "O novo status é obrigatório")
        StatusPedido status
		
		) {}
