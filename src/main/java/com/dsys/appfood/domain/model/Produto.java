package com.dsys.appfood.domain.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.*;


@Entity
@Table(name = "produto")
public class Produto {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(nullable = false)
	private String nome;
	
	@ManyToOne
	@JoinColumn(name = "categoria_id")
	private Categoria categoria;
	
	@OneToMany(mappedBy = "produto", cascade = CascadeType.ALL)
	private List<PrecoVariavel> precosVariaveis = new ArrayList<>();
	
	public Produto() {
		
	}

// CONSTRUTORES

	public Produto(String nome, Categoria categoria, List<PrecoVariavel> precosVariaveis) {
		this.nome = nome;
		this.categoria = categoria;
		this.precosVariaveis = precosVariaveis;
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

	public Categoria getCategoria() {
		return categoria;
	}

	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}


	public List<PrecoVariavel> getPrecosVariaveis() {
		return precosVariaveis;
	}


	public void setPrecosVariaveis(List<PrecoVariavel> precosVariaveis) {
		this.precosVariaveis = precosVariaveis;
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
		Produto other = (Produto) obj;
		return Objects.equals(id, other.id);
	}
	

}
