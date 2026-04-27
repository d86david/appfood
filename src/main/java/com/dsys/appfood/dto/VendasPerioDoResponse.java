package com.dsys.appfood.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import com.dsys.appfood.domain.enums.TipoPedido;

public record VendasPerioDoResponse (

	LocalDate dataInicio,
	LocalDate dataFim ,
	long totalPedidos ,
	BigDecimal faturamentoBruto,
	BigDecimal totalDescontos,
	BigDecimal faturamentoLiquido,
	BigDecimal ticketMedio,
	Map<TipoPedido, Long> pedidosPorTipo
	){	}