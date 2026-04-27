package com.dsys.appfood.dto;

import java.math.BigDecimal;

public record ProdutoVendidoResponse (
	
	 Integer produtoId,
     String nome,
     int quantidade,
     BigDecimal faturamento
     
     ){
	
}