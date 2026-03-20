package com.dsys.appfood.domain.model;

import java.math.BigDecimal;
import java.util.Objects;

import jakarta.persistence.*;

/**
 * Entidade que representa o valor de um produto baseado em seu tamanho. Esta
 * classe é fundamental para a regra de negócio da pizzaria, permitindo que o
 * mesmo sabor tenha preços diferentes (Ex: Pizza G vs Pizza M).
 */
@Entity
@Table(name = "preco_variavel")
public class PrecoVariavel {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private BigDecimal valor;

	@ManyToOne
	@JoinColumn(name = "produto_id")
	private Produto produto;

	@ManyToOne
	@JoinColumn(name = "tamanho_id")
	private Tamanho tamanho;

	// ===========================================
	// CONSTRUTORES
	// ===========================================

	public PrecoVariavel() {

	}

	public PrecoVariavel(Produto produto, Tamanho tamanho, BigDecimal valor) {
		
		configurarProdutoPorTamanho( produto,  tamanho,  valor);
	}

	// ===========================================
	// GETTERS
	// ===========================================

	public Integer getId() {
		return id;
	}

	public BigDecimal getValor() {
		return valor;
	}

	public Produto getProduto() {
		return produto;
	}

	public Tamanho getTamanho() {
		return tamanho;
	}

	public void setTamanho(Tamanho tamanho) {
		this.tamanho = tamanho;
	}

	// ===========================================
	// ASSOCIAR PROÇO DO PRODUTO AO TAMANHO
	// ===========================================
	private void configurarProdutoPorTamanho(Produto produto, Tamanho tamanho, BigDecimal valor) {

		// Verifica se o produto está nulo
		if (produto == null) {
			throw new IllegalArgumentException("O produto deve ser informado");
		}

		// Verifica se o tamanho está nulo
		if (tamanho == null) {
			throw new IllegalArgumentException("O tamanho deve ser informado");
		}
		
		//Verifica se o Valor não é negativo
		if(valor.signum() == -1 ) {
			throw new IllegalArgumentException("O valor não pode ser negativo");
		}

		this.produto = produto;
		this.tamanho = tamanho;
		this.valor = valor;
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
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PrecoVariavel other = (PrecoVariavel) obj;
		return Objects.equals(id, other.id);
	}

}
