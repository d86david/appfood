package com.dsys.appfood.domain.model;

import java.math.BigDecimal;
import java.nio.channels.IllegalSelectorException;
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

	@Column(name = "imprime_cozinha")
	private boolean imprimeCozinha;

	@ManyToOne
	@JoinColumn(name = "categoria_id")
	private Categoria categoria;

	@OneToMany(mappedBy = "produto", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<PrecoVariavel> precosVariaveis = new ArrayList<>();

	// ==============================
	// CONSTRUTORES
	// ==============================

	public Produto() {

	}

	public Produto(String nome, Categoria categoria,boolean imprimeCozinha) {
		this.nome = nome;
		this.categoria = categoria;
		this.imprimeCozinha = imprimeCozinha;
	}

	// ===============================
	// GETTERS E SETTERS
	// ===============================

	public Integer getId() {
		return id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = Objects.requireNonNull(nome, "O nome não pode ser nulo");
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

	public boolean isImprimeCozinha() {
		return imprimeCozinha;
	}

	public void setImprimeCozinha(boolean imprimeCozinha) {
		this.imprimeCozinha = imprimeCozinha;
	}

	/**
	 * Busca o preço do produto para um determinado tamanho.
	 *
	 * Este método é utilizado SOMENTE no momento da criação de um pedido.
	 *
	 * Depois que o pedido é criado, o preço deve ser armazenado como SNAPSHOT
	 * dentro do pedido.
	 */
	public BigDecimal obterPrecoParaTamanho(Tamanho tamanho) {
		return precosVariaveis.stream()
				.filter(p -> p.getTamanho().equals(tamanho))
				.map(PrecoVariavel::getValor)
				.findFirst().orElseThrow(() -> new IllegalSelectorException());
	}
	
	/**
	 * Adiciona um preço a Tamanho .
     * Este método garante a consistência da associação
     * bidirecional entre Pedido e PrecoVariavel.
	 * @param preco
	 */
	public void adicionarPreco(PrecoVariavel preco) {
		Objects.requireNonNull(preco, "O preço não pode ser nulo");
		preco.associarProduto(this);
		precosVariaveis.add(preco);	
	}

	// ===============================
	// HASHCODE E EQUALS
	// ===============================

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
