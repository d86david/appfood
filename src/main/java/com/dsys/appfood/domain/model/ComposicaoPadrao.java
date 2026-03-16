package com.dsys.appfood.domain.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Representa a receita padrão de um produto.
 *
 * Exemplo:
 *
 * Produto: Pizza Calabresa
 *
 * Ingredientes: - massa - molho - queijo - calabresa - cebola
 *
 * Essa composição NÃO deve ser alterada pelo pedido do cliente.
 *
 * Customizações são registradas separadamente em ItemCustomizacao.
 */
@Entity
@Table(name = "composicao_padrao")
public class ComposicaoPadrao {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	/**
	 * Produto ao qual esta composição pertence.
	 */
	@ManyToOne
	@JoinColumn(name = "produto_id")
	private Produto produto;

	/**
	 * Ingredientes padrão da receita.
	 */
	@ManyToMany
	@JoinTable(name = "composicao_ingrediente", joinColumns = @JoinColumn(name = "composicao_id"), inverseJoinColumns = @JoinColumn(name = "ingrediente_id"))
	private List<Ingrediente> ingredientes = new ArrayList<>();

//CONSTRUTOR
	public ComposicaoPadrao() {

	}

// GETTERS E SETTERS

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Produto getProduto() {
		return produto;
	}

	public void setProduto(Produto produto) {
		this.produto = produto;
	}

	public List<Ingrediente> getIngredientes() {
		return ingredientes;
	}



	 /**
     * Adiciona ingrediente à composição padrão.
     */
	public void adicionarIngrediente(Ingrediente ingrediente) {
		Objects.requireNonNull(ingrediente, "O Ingrediente não pode ser nulo");
		
		ingredientes.add(ingrediente);
	}
	
	/**
     * Remove ingrediente da composição padrão.
     */
    public void removerIngrediente(Ingrediente ingrediente){

        ingredientes.remove(ingrediente);
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
		ComposicaoPadrao other = (ComposicaoPadrao) obj;
		return Objects.equals(id, other.id);
	}

}
