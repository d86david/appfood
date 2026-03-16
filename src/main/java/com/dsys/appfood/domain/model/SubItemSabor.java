package com.dsys.appfood.domain.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.*;

/**
 * Representa um sabor dentro de uma pizza.
 * Cada sabor pode possuir customizações
 * como adiocnar ou remover ingredientes 
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

	// CONSTRUTORES
	
	public SubItemSabor() {
		
	}
	
	public SubItemSabor(ItemPedido item, Produto produto, List<ItemCustomizacao> customizacoes) {
		super();
		this.item = item;
		this.produto = produto;
		this.customizacoes = customizacoes;
	}

// GETTERS E SETTERS

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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


	/**
	 * Método que retorna o preço para um determinado tamanho
	 */
	public BigDecimal getPrecoParaTamanho(Tamanho tamanho) {
		return produto.getPrecosVariaveis().stream()
				.filter(pv -> pv.getTamanho().equals(tamanho))
				.map(PrecoVariavel::getValor)
				.findFirst()
				.orElse(BigDecimal.ZERO);
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
		
		Objects.requireNonNull(customizacao);
		
		customizacao.associarSubItem(this);
		
		customizacoes.add(customizacao);
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
	

//HASHCODE E EQUALS


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
