package com.dsys.appfood.domain.model;

import java.math.BigDecimal;
import java.util.Objects;

import jakarta.persistence.*;


@Entity
@Table(name = "entregador")
public class Entregador {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(nullable = false)
	private String nome; 
	
	private String telefone;
	
	@Column(name = "valor_por_entrega")
	private BigDecimal valorPorEntrega;

// CONSTRUTORES
	
	public Entregador() {
		
	}
	
	public Entregador(String nome, String telefone, BigDecimal valorPorEntrega) {
		this.nome = nome;
		this.telefone = telefone;
		this.valorPorEntrega = valorPorEntrega;
	}
	
// GETTERS E SETTERS

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	public BigDecimal getValorPorEntrega() {
		return valorPorEntrega;
	}

	public void setValorPorEntrega(BigDecimal valorPorEntrega) {
		this.valorPorEntrega = valorPorEntrega;
	}
	
// HASHCODE E EQUALS

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
		Entregador other = (Entregador) obj;
		return Objects.equals(id, other.id);
	}
	
}
