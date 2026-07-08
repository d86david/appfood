package com.dsys.appfood.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record IndicadoresDiariosResponse(

		LocalDate data,
		long totalPedidos,
		long finalizados,
		long cancelados,
		long emAndamento,
		BigDecimal faturamento,
		BigDecimal ticketMedio,
		long delivery,
		long balcao,
		long mesa) {}