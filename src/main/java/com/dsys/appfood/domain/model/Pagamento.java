package com.dsys.appfood.domain.model;

import java.math.BigDecimal;
import java.util.Objects;

import com.dsys.appfood.domain.enums.FormaPagamento;

import jakarta.persistence.*;

/**
 * Entidade responsavel por ligar o pedido ao Caixa, e encerrar o clico do
 * pedido
 * 
 * 
 */
@Entity
@Table(name = "pagamento")
public class Pagamento {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "pedido_id")
	private Pedido pedido;

	@ManyToOne
	@JoinColumn(name = "caixa_id")
	private Caixa caixa;

	@Enumerated(EnumType.STRING)
	private FormaPagamento formaPagamento;

	private BigDecimal valor;

	private BigDecimal troco;

//CONSTRUTORES
	public Pagamento() {

	}

	public Pagamento(Pedido pedido, Caixa caixa, FormaPagamento formaPagamento, BigDecimal valor) {
		if (valor == null || valor.signum() == -1) {
			throw new IllegalArgumentException("O Valor não pode ser nulo ou negativo");
		}
		this.caixa = caixa;
		this.formaPagamento = formaPagamento;
		this.valor = valor;
		
	}

//GETTERS E SETTERS

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Pedido getPedido() {
		return pedido;
	}

	public Caixa getCaixa() {
		return caixa;
	}

	public void setCaixa(Caixa caixa) {
		this.caixa = caixa;
	}

	public FormaPagamento getFormaPagamento() {
		return formaPagamento;
	}

	public BigDecimal getValor() {
		return valor;
	}

	public BigDecimal getTroco() {
		return troco;
	}
	
	void associarPedido(Pedido pedido) {
		this.pedido = pedido;
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
		Pagamento other = (Pagamento) obj;
		return Objects.equals(id, other.id);
	}

	/**
	 * Verifica se o pagamento cobre o total do pedido. Utíl para pagamentos
	 * parciais ou validação final.
	 */
	public boolean isPagamentoSuficiente() {
		if (this.valor == null || this.pedido == null)
			return false;
		return this.valor.compareTo(pedido.getValorLiquido()) >= 0;
	}

}
