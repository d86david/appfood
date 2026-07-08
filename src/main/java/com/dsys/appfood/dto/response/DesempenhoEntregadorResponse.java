package com.dsys.appfood.dto.response;

import java.math.BigDecimal;

public record DesempenhoEntregadorResponse (

	 Integer entregadorId,
     String nome,
     long totalEntregas,
     BigDecimal totalTaxas

     ) {}