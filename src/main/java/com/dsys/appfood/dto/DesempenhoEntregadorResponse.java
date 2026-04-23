package com.dsys.appfood.dto;

import java.math.BigDecimal;

public class DesempenhoEntregadorResponse {
	
	 private Integer entregadorId;
     private String nome;
     private long totalEntregas;
     private BigDecimal totalTaxas;
     
     // CONSTRUTORES 
     
     public DesempenhoEntregadorResponse() {
    	 
     }
     
	 public DesempenhoEntregadorResponse(Integer entregadorId, String nome, long totalEntregas, BigDecimal totalTaxas) {
		super();
		this.entregadorId = entregadorId;
		this.nome = nome;
		this.totalEntregas = totalEntregas;
		this.totalTaxas = totalTaxas;
	 }
	 
	 // GETTERES

	 public Integer getEntregadorId() {
		 return entregadorId;
	 }

	 public String getNome() {
		 return nome;
	 }

	 public long getTotalEntregas() {
		 return totalEntregas;
	 }

	 public BigDecimal getTotalTaxas() {
		 return totalTaxas;
	 }
     
	 
     

}
