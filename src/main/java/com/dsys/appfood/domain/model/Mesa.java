package com.dsys.appfood.domain.model;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "mesa")
public class Mesa {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(nullable = false, unique = true)
	private Integer numero;

	@Column(nullable = false)
	private boolean ocupada = false;

	private Integer capacidade;

	private boolean ativa;

	// ===========================================
	// CONSTRUTORES
	// ===========================================

	public Mesa() {

	}

	public Mesa(Integer numero, boolean ocupada, Integer capacidade) {
		this.numero = numero;
		this.ocupada = ocupada;
		this.capacidade = capacidade;
	}

	// ===========================================
	// GETTERS E SETTERS
	// ===========================================

	public Integer getId() {
		return id;
	}

	public Integer getNumero() {
		return numero;
	}

	public void setNumero(Integer numero) {
		this.numero = numero;
	}

	public boolean isOcupada() {
		return ocupada;
	}

	public Integer getCapacidade() {
		return capacidade;
	}

	public void setCapacidade(Integer capacidade) {
		this.capacidade = capacidade;
	}

	public boolean isAtiva() {
		return ativa;
	}

	public void setAtiva(boolean ativa) {
		this.ativa = ativa;
	}

	// ===========================================
	// METODOS AUXILIARES
	// ===========================================

	public void ocupar() {
		if (this.ocupada) {
			throw new IllegalStateException("Mesa já está ocupada");
		}
		this.ocupada = true;
	}

	public void liberar() {
		if (!this.ocupada) {
			throw new IllegalStateException("Mesa já está livre");
		}
		this.ocupada = false;
	}

	// ===========================================
	// HASHCODE E EQUALS
	// ===========================================

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		Mesa other = (Mesa) obj;
		return Objects.equals(id, other.id);
	}
}
