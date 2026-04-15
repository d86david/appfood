package com.dsys.appfood.domain.model;

import java.util.Objects;

import com.dsys.appfood.domain.enums.FormaPagamento;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Representa o mapeamento entre uma forma de pagamento e a conta corrente correspondente.
 * Esta entidade permite que o destino dos valores seja configurado via banco de dados.
 */
@Entity
@Table(name = "configuracao_pagamento")
public class ConfiguracaoPagamento {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	/** Define a forma de pagamento (PIX, Crédito, etc.) */
	@Enumerated(EnumType.STRING)
	private FormaPagamento formaPagamento;
	
	/** Define a conta corrente onde o dinheiro será creditado */
	@ManyToOne
	@JoinColumn(name = "conta_corrente_id")
	private ContaCorrente contaCorrente;
	
	
	//===========================================
	// CONSTRUTORES
	//===========================================
	public ConfiguracaoPagamento() {
		
	}

	public ConfiguracaoPagamento(FormaPagamento formaPagamento, ContaCorrente contaCorrente) {
		super();
		this.formaPagamento = formaPagamento;
		this.contaCorrente = contaCorrente;
	}
	
	
	//===========================================
	// GETTERS E SETTERS
	//===========================================

	public FormaPagamento getFormaPagamento() {
		return formaPagamento;
	}

	public void setFormaPagamento(FormaPagamento formaPagamento) {
		this.formaPagamento = formaPagamento;
	}

	public ContaCorrente getContaCorrente() {
		return contaCorrente;
	}

	public void setContaCorrente(ContaCorrente contaCorrente) {
		this.contaCorrente = contaCorrente;
	}

	public Integer getId() {
		return id;
	}

	//===========================================
	//HASHCODE E EQUALS
	//===========================================
	
	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ConfiguracaoPagamento other = (ConfiguracaoPagamento) obj;
		return Objects.equals(id, other.id);
	}
}
