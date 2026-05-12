package com.dsys.appfood.dto.response;

import java.math.BigDecimal;

public record ProdutoVendidoResponse (
	
	 Integer produtoId,
     String nome,
     int quantidade,
     BigDecimal faturamento
     
     ){
	
}