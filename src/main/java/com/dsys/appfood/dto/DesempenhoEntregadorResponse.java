package com.dsys.appfood.dto;

import java.math.BigDecimal;

public record DesempenhoEntregadorResponse (
	
	 Integer entregadorId,
     String nome,
     long totalEntregas,
     BigDecimal totalTaxas
    
     ) {}