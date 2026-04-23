package com.dsys.appfood.dto;

import java.math.BigDecimal;

public class ProdutoVendidoResponse {
	
	 private Integer produtoId;
     private String nome;
     private int quantidade;
     private BigDecimal faturamento;
     
     
     // CONSTRUTORES
     
     public ProdutoVendidoResponse() {
    	 
     }

	 public ProdutoVendidoResponse(Integer produtoId, String nome, int quantidade, BigDecimal faturamento) {
		super();
		this.produtoId = produtoId;
		this.nome = nome;
		this.quantidade = quantidade;
		this.faturamento = faturamento;
	 }
	 
	 // GETTERS

	 public Integer getProdutoId() {
		 return produtoId;
	 }

	 public String getNome() {
		 return nome;
	 }

	 public int getQuantidade() {
		 return quantidade;
	 }

	 public BigDecimal getFaturamento() {
		 return faturamento;
	 }
     
}
