package com.dsys.appfood.domain.model;

import java.util.Objects;

import jakarta.persistence.*;


@Entity
@Table(name = "tamanho")
public class Tamanho {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(nullable = false)
	private String nome;

	//=====================================
	// CONSTRUTORES
	//=====================================
	
	public Tamanho() {
	}
	
	public Tamanho(String nome) {
		this.nome = nome;
	}

	//=====================================
	// GETTERS E SETTERS
	//=====================================

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

	//=====================================
	// HASHCODE E EQUALS
	//=====================================
	
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
		Tamanho other = (Tamanho) obj;
		return Objects.equals(id, other.id);
	}
	
}
