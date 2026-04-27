package com.dsys.appfood.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.dsys.appfood.domain.enums.StatusPedido;
import com.dsys.appfood.domain.enums.TipoPedido;
import com.dsys.appfood.domain.model.Pedido;

public record PedidoResumoResponse (
	
	Integer id,
    TipoPedido tipo,
    StatusPedido status,
    LocalDateTime dataHora,
    String cliente,
    BigDecimal total
    ) {   
    
	public static PedidoResumoResponse from( Pedido pedido) {
    	return new  PedidoResumoResponse(
    			
    			pedido.getId(),
    			pedido.getTipo(),
    			pedido.getStatusAtual(),
    			pedido.getDtHoraAbertura(),
    			pedido.getCliente().getNome(),
    			pedido.getValorLiquido()
    			
    			);
    }
}