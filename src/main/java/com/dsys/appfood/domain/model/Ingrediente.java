package com.dsys.appfood.domain.model;

import java.math.BigDecimal;
import java.util.Objects;

import jakarta.persistence.*;

@Entity
@Table(name = "ingrediente")
public class Ingrediente {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(nullable = false)
	private String nome;

	/**
	 * Valor adicional cobrado caso seja adicionado ao pedido.
	 */
	@Column(name = "valor_adicional")
	private BigDecimal valorAdicional;

	//===========================================
	// CONSTRUTORES
	//===========================================

	public Ingrediente() {

	}

	public Ingrediente(String nome, BigDecimal valorAdicional) {
		this.nome = nome;
		this.valorAdicional = valorAdicional;
	}

	//===========================================
	// GETTERS E SETTERS
	//===========================================

	public Integer getId() {
		return id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public BigDecimal getValorAdicional() {
		return valorAdicional;
	}

	/**
	 * Atualiza o valor adicional do ingrediente.
	 *
	 * Esse método deve ser usado apenas por administração do sistema.
	 */
	public void atualizarValorAdicional(BigDecimal novoValor) {
		if (novoValor.compareTo(BigDecimal.ZERO) < 0) {
			throw new IllegalArgumentException("Valor inválido.");
		}

		this.valorAdicional = novoValor;
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
		Ingrediente other = (Ingrediente) obj;
		return Objects.equals(id, other.id);
	}

}
