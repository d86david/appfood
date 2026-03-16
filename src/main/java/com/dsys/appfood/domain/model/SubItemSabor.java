package com.dsys.appfood.domain.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.*;

/**
 * Representa um sabor dentro de um ItemPedido.
 * 
 * Um ItemPedido pode representar uma pizza inteira, meira pizza o multiplos sabores
 * 
 * Cada SubItemSabor representa um sabor específico da pizza escolhida
 * 
 * Cada sabor pode possuir customizações
 * - adiocnar ingrediente 
 * - remover ingrediente 
 * 
 * Esta Entidade também armazena um SNAPSHOT do preço do produto no momento da venda
 * Isso garante que alterações futuras no catálogo não alterem pedidos ja realizados.
 */
@Entity
@Table(name = "subitem_sabor")
public class SubItemSabor {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	/**
	 * ItemPedido ao qual este sabor pertence.
	 */
	@ManyToOne
	@JoinColumn(name = "item_id")
	private ItemPedido item;
	
	/**
	 * Produto do catálogo.
	 * Ex:
	 *  - Pizza Caabreza
	 *  - Pizza Mussarela
	 */
	@ManyToOne
	@JoinColumn(name = "produto_id")
	private Produto produto;
	
	/**
	 * Lista de customizações aplicadas ao sabor.
	 */
	@OneToMany(mappedBy = "subItemSabor", cascade = CascadeType.ALL)
	private List<ItemCustomizacao> customizacoes = new ArrayList<>();
	
	/**
	 * SNAPSHOT do preço do sabor no memento da criação do pedido e nunca deve mudar
	 */
	@Column(name="preco_sabor")
	private BigDecimal precoSabor;

	//=====================================
	// CONSTRUTORES
	//=====================================
	
	public SubItemSabor() {
		
	}
	
	public SubItemSabor(Produto produto, BigDecimal precoSabor) {
		
		this.produto = Objects.requireNonNull(produto, "O Produto não pode ser nulo");
		this.precoSabor = Objects.requireNonNull(precoSabor, "O preço não pode ser nulo");
	}

	//=====================================
	// GETTERS E SETTERS
	//=====================================

	public Integer getId() {
		return id;
	}


	public ItemPedido getItem() {
		return item;
	}

	public Produto getProduto() {
		return produto;
	}

	public void setProduto(Produto produto) {
		this.produto = produto;
	}
	

	public List<ItemCustomizacao> getCustomizacoes() {
		return customizacoes;
	}
	
	public BigDecimal getPrecoSabor() {
		return this.precoSabor;
	}

	
	/**
	 * Método para Associar um Item ao ItemPedido
	 * @return
	 */
	void associarItem(ItemPedido item) {
		this.item = item;
	}
	
	/**
	 * Método reponsavel por por adicionar uma customização
	 * Este método garante a integridade da relação entre SubItem e ItemCustomizacao
	 * @return
	 */
	public void adicionarCustomizacao(ItemCustomizacao customizacao) {
		
		Objects.requireNonNull(customizacao, "Customização não pode ser nula");
		
		customizacao.associarSubItem(this);
		
		customizacoes.add(customizacao);
	}
	
	/**
	 * Remove uma customização do sabor
	 */
	public void removerCustomizacao(ItemCustomizacao customizacao) {
		customizacoes.remove(customizacao);
	}
	
	/**
	 * Método para somar o valor de todas as customizações
	 * @return
	 */
	public BigDecimal calcularTotalCustomizacoes() {
		//percorre a lista de customizações e soma os valores cobrados
		return customizacoes.stream()
				.map(ItemCustomizacao::getValorCobrado)
				.filter(Objects::nonNull)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
	}
	
	/**
	 * Calcula o preco final deste sabor
	 * formula: precoSnapshot + customizações 
	 * 
	 * @return o preço final do sabor
	 */
	
	
	public BigDecimal calculaPrecoSabor() {
		return precoSabor.add(calcularTotalCustomizacoes());
	}

	//=====================================
	//HASHCODE E EQUALS
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
		SubItemSabor other = (SubItemSabor) obj;
		return Objects.equals(id, other.id);
	}

}
