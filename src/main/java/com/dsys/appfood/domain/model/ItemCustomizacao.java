package com.dsys.appfood.domain.model;

import java.math.BigDecimal;
import java.util.Objects;

import com.dsys.appfood.domain.enums.TipoCustomizacao;

import jakarta.persistence.*;
/**
 * Essa entidade representa uma modificação aplicada a um sabor
 */
@Entity
@Table(name = "item_customizacao")
public class ItemCustomizacao {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	/**
     * SubItemSabor ao qual esta customização pertence.
     */
	@ManyToOne
	@JoinColumn(name = "subitem_id")
	private SubItemSabor subItemSabor;
	
	/**
     * ItemPedido ao qual esta customização Global pertence.
     */
	@ManyToOne
	@JoinColumn(name = "itemPedido_id")
	private ItemPedido itemPedido;
	
	/**
     * Borda envolvida na customização.
     */
	@ManyToOne
	@JoinColumn(name = "borda_id")
	private Borda borda;

	/**
     * Ingrediente envolvido na customização.
     */
	@ManyToOne
	@JoinColumn(name = "ingrediente_id")
	private Ingrediente ingrediente;

	/**
     * Tipo da customização.
     *
     * ADICIONAR
     * REMOVER
     */
	@Enumerated(EnumType.STRING)
	private TipoCustomizacao tipoCustomizacao;

	/**
     * Valor cobrado pela customização.
     */
	private BigDecimal valorCobrado;

	//===========================================
	// CONSTRUTORES
	//===========================================

	public ItemCustomizacao() {

	}

	public ItemCustomizacao(SubItemSabor subItemSabor, Ingrediente ingrediente, TipoCustomizacao tipoCustomizacao,
			BigDecimal valorCobrado) {
		this.subItemSabor = subItemSabor;
		this.ingrediente = ingrediente;
		this.tipoCustomizacao = tipoCustomizacao;
		adicionarValorACustomizacao(valorCobrado);
	}

	//===========================================
	// GETTERS E SETTERS
	//===========================================

	public Integer getId() {
		return id;
	}


	public SubItemSabor getSubItemSabor() {
		return subItemSabor;
	}

	public Ingrediente getIngrediente() {
		return ingrediente;
	}

	public void setIngrediente(Ingrediente ingrediente) {
		this.ingrediente = ingrediente;
	}
	
	public Borda getBorda() {
		return borda;
	}

	public void setBorda(Borda borda) {
		this.borda = borda;
	}

	public TipoCustomizacao getTipoCustomizacao() {
		return tipoCustomizacao;
	}

	public void setTipoCustomizacao(TipoCustomizacao tipoCustomizacao) {
		this.tipoCustomizacao = tipoCustomizacao;
	}

	public BigDecimal getValorCobrado() {
		return valorCobrado;
	}
	
	/*
	 * Método para associar ItemCustomização ao SubItemSabor
	 */
	public void associarSubItem(SubItemSabor subItem) {
		this.subItemSabor = subItem;
	}
	
	/*
	 * Método para associar ItemCustomização ao ItemPedido
	 */
	public void associarItemPedido(ItemPedido itemPedido) {
		this.itemPedido = itemPedido;
	}
	
	/**
	 * Método para adicionar valor 
	 */
	public void adicionarValorACustomizacao(BigDecimal valorCobrado) {
		if(valorCobrado.signum() < 0) {
			throw new IllegalAccessError("O valor da Customização não pode ser negativo");
		}
		this.valorCobrado = valorCobrado;
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
		ItemCustomizacao other = (ItemCustomizacao) obj;
		return Objects.equals(id, other.id);
	}
	

}
