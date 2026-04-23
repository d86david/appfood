package com.dsys.appfood.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.dsys.appfood.domain.enums.StatusPedido;
import com.dsys.appfood.domain.enums.TipoPedido;

public class PedidoResumoResponse {
	
	private Integer id;
    private TipoPedido tipo;
    private StatusPedido status;
    private LocalDateTime dataHora;
    private String cliente;
    private BigDecimal total;
    
    
    // CONSTRUTURES
    
    public PedidoResumoResponse() {
    	
    }
    
    public PedidoResumoResponse(Integer id, TipoPedido tipo, StatusPedido status, LocalDateTime dataHora, String cliente, BigDecimal total) {
        this.id = id;
        this.tipo = tipo;
        this.status = status;
        this.dataHora = dataHora;
        this.cliente = cliente;
        this.total = total;
    }
    
    // GETTERS

	public Integer getId() {
		return id;
	}

	public TipoPedido getTipo() {
		return tipo;
	}

	public StatusPedido getStatus() {
		return status;
	}

	public LocalDateTime getDataHora() {
		return dataHora;
	}

	public String getCliente() {
		return cliente;
	}

	public BigDecimal getTotal() {
		return total;
	}
}
