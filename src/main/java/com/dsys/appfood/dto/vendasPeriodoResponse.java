package com.dsys.appfood.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import com.dsys.appfood.domain.enums.TipoPedido;

public class vendasPeriodoResponse {

	private LocalDate dataInicio;
	private LocalDate dataFim = null;
	private long totalPedidos = 0L;
	private BigDecimal faturamentoBruto;
	private BigDecimal totalDescontos;
	private BigDecimal faturamentoLiquido;
	private BigDecimal ticketMedio;
	private Map<TipoPedido, Long> pedidosPorTipo;

	// CONSTRUTORES

	public vendasPeriodoResponse() {

	}

	public vendasPeriodoResponse(LocalDate dataInicio, LocalDate dataFim, long totalPedidos,
			BigDecimal faturamentoBruto, BigDecimal totalDescontos, BigDecimal faturamentoLiquido,
			BigDecimal ticketMedio, Map<TipoPedido, Long> pedidosPorTipo) {

		this.dataInicio = dataInicio;
		this.dataFim = dataFim;
		this.totalPedidos = totalPedidos;
		this.faturamentoBruto = faturamentoBruto;
		this.totalDescontos = totalDescontos;
		this.faturamentoLiquido = faturamentoLiquido;
		this.ticketMedio = ticketMedio;
		this.pedidosPorTipo = pedidosPorTipo;
	}

//GETTERS 

	public LocalDate getDataInicio() {
		return dataInicio;
	}

	public LocalDate getDataFim() {
		return dataFim;
	}

	public long getTotalPedidos() {
		return totalPedidos;
	}

	public BigDecimal getFaturamentoBruto() {
		return faturamentoBruto;
	}

	public BigDecimal getTotalDescontos() {
		return totalDescontos;
	}

	public BigDecimal getFaturamentoLiquido() {
		return faturamentoLiquido;
	}

	public BigDecimal getTicketMedio() {
		return ticketMedio;
	}

	public Map<TipoPedido, Long> getPedidosPorTipo() {
		return pedidosPorTipo;
	}

}
