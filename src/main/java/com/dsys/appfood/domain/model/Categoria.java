package com.dsys.appfood.domain.model;

import java.util.Objects;

import jakarta.persistence.*;

@Entity
@Table(name = "categoria")
public class Categoria {

	@Id
	@GeneratedValue
	private Integer id;

	@Column(nullable = false)
	private String nome;

// CONTRUTORES
	public Categoria() {

	}

	public Categoria(String nome) {
		this.nome = nome;
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
		Categoria other = (Categoria) obj;
		return Objects.equals(id, other.id);
	}
	

}
