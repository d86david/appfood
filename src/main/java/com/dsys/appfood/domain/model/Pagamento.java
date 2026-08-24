package com.dsys.appfood.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

import com.dsys.appfood.domain.enums.FormaPagamento;
import com.dsys.appfood.exception.NegocioException;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Entidade responsavel por ligar o pedido a do Sessao do Caixa, e encerrar o clico do
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
	@JoinColumn(name = "sessao_caixa_id")
	private SessaoCaixa sessaoCaixa;

	@Enumerated(EnumType.STRING)
	private FormaPagamento formaPagamento;

	private BigDecimal valor;

	private BigDecimal troco;

	@Column(name = "data_hora")
	LocalDateTime dataHora;
	
	private Boolean estornado = false;
	
	@Column(name = "data_estorno")
	LocalDateTime dataEstorno;
	
	@Column(name = "motivo_estorno")
	private String motivoEstorno;

	@ManyToOne
	@JoinColumn(name = "operador_id")
	private Usuario operador;

	// ===========================================
	// CONSTRUTORES
	// ===========================================

	public Pagamento() {

	}

	// ===========================================
	// GETTERS E SETTERS
	// ===========================================

	public Integer getId() {
		return id;
	}

	public Pedido getPedido() {
		return pedido;
	}

	public SessaoCaixa getSessaoCaixa() {
		return sessaoCaixa;
	}

	public void setSessaoCaixa(SessaoCaixa sessaoCaixa) {
		this.sessaoCaixa = sessaoCaixa;
	}

	public FormaPagamento getFormaPagamento() {
		return formaPagamento;
	}

	public void setFormaPagamento(FormaPagamento formaPagamento) {
		this.formaPagamento = formaPagamento;
	}

	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

	public BigDecimal getTroco() {
		return troco;
	}

	public void associarPedido(Pedido pedido) {
		this.pedido = pedido;
	}

	public Usuario getOperador() {
		return operador;
	}

	public void setOperador(Usuario operador) {
		this.operador = operador;
	}
	
	
	public Boolean getEstornado() {
		return estornado;
	}

	public LocalDateTime getDataEstorno() {
		return dataEstorno;
	}

	public String getMotivoEstorno() {
		return motivoEstorno;
	}

	public Boolean isEstornado() {
		return estornado;
	}

	public LocalDateTime getDataHora() {
		return dataHora;
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
		if (this == obj) {
			return true;
		}
		if ((obj == null) || (getClass() != obj.getClass())) {
			return false;
		}
		Pagamento other = (Pagamento) obj;
		return Objects.equals(id, other.id);
	}

	/**
	 * Verifica se o pagamento cobre o total do pedido. Utíl para pagamentos
	 * parciais ou validação final.
	 */
	public boolean isPagamentoSuficiente() {
		if (this.valor == null || this.pedido == null) {
			return false;
		}
		return this.valor.compareTo(pedido.getValorLiquido()) >= 0;
	}
	
	/**
	 * Marca este pagamento como estornado, sem apagá-lo.
	 *
	 */
	public void estornar(String motivo) {
	    
		// Valida se o pagamento ja está estornado
		if(estornado) {
	    	throw new NegocioException("Não é possivel estornar mais de uma vez o mesmo lançamento!");
	    }
		
		// Valida se tem motivo 
		if(motivo.isBlank()) {
			throw new NegocioException("É necessario informar um motivo para o estorno");
		}
		
	    this.estornado = true;
	    this.dataEstorno = LocalDateTime.now();
	    this.motivoEstorno = motivo;
	}

}
