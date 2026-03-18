package com.dsys.appfood.domain.model;


import java.util.Objects;

import jakarta.persistence.*;

@Entity
@Table(name = "cliente")
public class Cliente {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(nullable = false)
	private String nome;

	private String telefone;

	@OneToOne
	@JoinColumn(name = "endereco_id")
	private Endereco endereco;

	//===========================================
	// CONSTRUTORES
	//===========================================
	public Cliente() {
	}

	public Cliente(String nome, String telefone,Endereco endereco) {
		this.nome = nome;
		this.telefone = telefone;
		this.endereco = endereco;
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

	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	public Endereco getEndereco() {
		return endereco;
	}

	public void setEndereco(Endereco endereco) {
		this.endereco = endereco;
	}

	//===========================================
	// HASHCODE E EQUALS
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
		Cliente other = (Cliente) obj;
		return Objects.equals(id, other.id);
	}

}
