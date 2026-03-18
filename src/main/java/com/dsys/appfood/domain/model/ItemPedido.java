package com.dsys.appfood.domain.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.*;

@Entity
@Table(name="item_pedido")
public class ItemPedido {
	/**
	 * Representa um item dentro de um Pedido.
	 *
	 * Em uma pizzaria, um ItemPedido normalmente representa
	 * UMA pizza completa, podendo conter:
	 *
	 * - 1 sabor (pizza inteira)
	 * - 2 sabores (meia a meia)
	 * - 3 sabores
	 *
	 * Cada sabor é representado por um SubItemSabor.
	 *
	 * Este design permite grande flexibilidade para modelar
	 * pizzas com múltiplos sabores e customizações.
	 * 
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	 /**
     * Pedido ao qual este item pertence.
     * Lado dono da relação no banco.
     */
	@ManyToOne
	@JoinColumn(name = "pedido_id")
	private Pedido pedido;
	
	/**
     * Tamanho da pizza (Pequena, Média, Grande).
     * O tamanho influencia diretamente no preço.
     */
	@ManyToOne
	@JoinColumn(name = "tamanho_id")
	private Tamanho tamanho;
	
	/**
     * Lista de sabores que compõem a pizza.
     * Exemplo:
     * Pizza Meio a Meio:
     * - Calabresa
     * - Frango
     */
	@OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
	private List<SubItemSabor> subItens = new ArrayList<>();

	//===========================================
	// CONSTRUTORES
	//===========================================
	
	public ItemPedido() {

	}

	public ItemPedido(Tamanho tamanho) {
		this.tamanho = tamanho;
	}

	//===========================================
	// GETTERS E SETTERS
	//===========================================

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Pedido getPedido() {
		return pedido;
	}

	public Tamanho getTamanho() {
		return tamanho;
	}

	public void setTamanho(Tamanho tamanho) {
		this.tamanho = tamanho;
	}

	public List<SubItemSabor> getSubItens() {
		return subItens;
	}

	//===========================================
	// MÉTODOS AUXILIARES
	//===========================================
	
	 /**
     * Associa este ItemPedido a um Pedido.
     * Este método NÃO é público para evitar manipulação externa da relação.
     */
    void associarPedido(Pedido pedido){
        this.pedido = pedido;
    }

    /**
     * Adiciona um sabor à pizza.
     * Este método garante a consistência da associação
     * bidirecional entre ItemPedido e SubItemSabor.
     *
     * @param sabor sabor que será adicionado à pizza
     */
    public void adicionarSabor(SubItemSabor sabor){

        Objects.requireNonNull(sabor,"O sabor não pode ser nulo");

        sabor.associarItem(this);

        subItens.add(sabor);
    }
    /**
     * Calcula o preço final da pizza.
     *
     * Regra clássica de pizzaria:
     *
     * - quando existem múltiplos sabores,
     *   cobra-se o valor do sabor MAIS CARO
     *
     * - todos os adicionais são somados
     */
	public BigDecimal calcularPrecoFinal() {
		BigDecimal precoMaiorSabor = BigDecimal.ZERO;
		BigDecimal totalAdicionais = BigDecimal.ZERO;
		
		for (SubItemSabor sabor : subItens) {
			//1 Pega p preço do sabor para o tamanho desta pizza
			BigDecimal precoSabor = sabor.getPrecoSabor();
			
			//2 Regra do Maior Valor: se este sabor for mais caro que o anterior, ele assume o posto
			if (precoSabor.compareTo(precoMaiorSabor) > 0) {
				precoMaiorSabor = precoSabor;
			}
			
			//3. Soma os adiconaisqcustomizações deste sabor específico
			totalAdicionais = totalAdicionais.add(sabor.calcularTotalCustomizacoes());
		}
		
		//retorna o maior valor entre os sabores + a soma de todos os adicionais
		return precoMaiorSabor.add(totalAdicionais);
	}

	//===========================================
	// HASCODE E EQUALS
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
		ItemPedido other = (ItemPedido) obj;
		return Objects.equals(id, other.id);
	}
}