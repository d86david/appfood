package com.dsys.appfood.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

import com.dsys.appfood.domain.enums.TipoMovimentacao;

import jakarta.persistence.*;

@Entity
@Table(name = "movimentacao_conta_corrente")
public class MovimentacaoContaCorrente {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "conta_id", nullable = false)
	private ContaCorrente conta;

	@Enumerated(EnumType.STRING)
	private TipoMovimentacao tipo;

	private BigDecimal valor;

	@Column(length = 500)
	private String descricao;

	@Column(name = "data_hora")
	private LocalDateTime dataHora = LocalDateTime.now();

	// Referência opcional ao pagamento que originou esta movimentação
	@ManyToOne
	@JoinColumn(name = "pagamento_id")
	private Pagamento pagamento;

	// Quem registrou/conciliou
	@ManyToOne
	@JoinColumn(name = "usuario_id")
	private Usuario usuario;

	// ===========================================
	// CONSTRUTORES
	// ===========================================

	public MovimentacaoContaCorrente() {

	}

	public MovimentacaoContaCorrente(ContaCorrente conta, TipoMovimentacao tipo, BigDecimal valor, String descricao,
			Usuario usuario) {

		this.conta = conta;
		this.tipo = tipo;
		this.valor = valor;
		this.descricao = descricao;
		this.usuario = usuario;
	}

	// ===========================================
	// GETTERS E SETTERS
	// ===========================================

	public Integer getId() {
		return id;
	}

	public ContaCorrente getConta() {
		return conta;
	}

	public void setConta(ContaCorrente conta) {
		this.conta = conta;
	}

	public TipoMovimentacao getTipo() {
		return tipo;
	}

	public void setTipo(TipoMovimentacao tipo) {
		this.tipo = tipo;
	}

	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public LocalDateTime getDataHora() {
		return dataHora;
	}

	public void setDataHora(LocalDateTime dataHora) {
		this.dataHora = dataHora;
	}

	public Pagamento getPagamento() {
		return pagamento;
	}

	public void setPagamento(Pagamento pagamento) {
		this.pagamento = pagamento;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	// ===========================================
	// HASHCODE E EQUALS
	// ===========================================
	
	 @Override
	    public boolean equals(Object o) {
	        if (this == o) return true;
	        if (!(o instanceof MovimentacaoContaCorrente)) return false;
	        MovimentacaoContaCorrente that = (MovimentacaoContaCorrente) o;
	        return Objects.equals(id, that.id);
	    }

	    @Override
	    public int hashCode() {
	        return Objects.hash(id);
	    }

}
