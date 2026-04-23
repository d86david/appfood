package com.dsys.appfood.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class IndicadoresDiariosResponse {
	
	private LocalDate data;
    private long totalPedidos;
    private long finalizados;
    private long cancelados;
    private long emAndamento;
    private BigDecimal faturamento;
    private BigDecimal ticketMedio;
    private long delivery;
    private long balcao;
    private long mesa;
    
 // CONSTRUTORES 
    
    public IndicadoresDiariosResponse() {
    	
    }
    
	public IndicadoresDiariosResponse(LocalDate data, long totalPedidos, long finalizados, long cancelados,
			long emAndamento, BigDecimal faturamento, BigDecimal ticketMedio, long delivery, long balcao, long mesa) {
		super();
		this.data = data;
		this.totalPedidos = totalPedidos;
		this.finalizados = finalizados;
		this.cancelados = cancelados;
		this.emAndamento = emAndamento;
		this.faturamento = faturamento;
		this.ticketMedio = ticketMedio;
		this.delivery = delivery;
		this.balcao = balcao;
		this.mesa = mesa;
	}
	
	// GETTERS

	public LocalDate getData() {
		return data;
	}

	public long getTotalPedidos() {
		return totalPedidos;
	}

	public long getFinalizados() {
		return finalizados;
	}

	public long getCancelados() {
		return cancelados;
	}

	public long getEmAndamento() {
		return emAndamento;
	}

	public BigDecimal getFaturamento() {
		return faturamento;
	}

	public BigDecimal getTicketMedio() {
		return ticketMedio;
	}

	public long getDelivery() {
		return delivery;
	}

	public long getBalcao() {
		return balcao;
	}

	public long getMesa() {
		return mesa;
	}
    
}
