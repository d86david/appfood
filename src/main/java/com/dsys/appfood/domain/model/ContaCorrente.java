package com.dsys.appfood.domain.model;

import java.math.BigDecimal;
import java.util.Objects;

import com.dsys.appfood.domain.enums.TipoConta;
import com.dsys.appfood.exception.NegocioException;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Representa uma conta bancária ou meio de pagamento eletronico Exemplos:
 * "Conta Banco do Brasil", "Máquina Cielo", "PIX Mercado Pago", etc
 */
@Entity
@Table(name = "conta_corrente")
public class ContaCorrente {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(nullable = false)
	private String nome;

	private String banco;

	@Column(length = 3)
	private String codBanco;

	@Column(length = 20)
	private String agencia;

	@Column(length = 20)
	private String conta;

	@Column(name = "saldo_atual")
	private BigDecimal saldoAtual = BigDecimal.ZERO;

	private boolean ativa = true;

	@Enumerated(EnumType.STRING)
	private TipoConta tipo;

	// ===========================================
	// CONSTRUTORES
	// ===========================================

	public ContaCorrente() {
	}

	public ContaCorrente(String nome, TipoConta tipo) {
		this.nome = nome;
		this.tipo = tipo;
	}

	//===========================================
	// GETTERS E SETTERS
	//===========================================

	public Integer getId() {
		return id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getBanco() {
		return banco;
	}

	public void setBanco(String banco) {
		this.banco = banco;
	}

	public String getCodBanco() {
		return codBanco;
	}

	public void setCodBanco(String codBanco) {
		this.codBanco = codBanco;
	}

	public String getAgencia() {
		return agencia;
	}

	public void setAgencia(String agencia) {
		this.agencia = agencia;
	}

	public String getConta() {
		return conta;
	}

	public void setConta(String conta) {
		this.conta = conta;
	}

	public BigDecimal getSaldoAtual() {
		return saldoAtual;
	}

	public void setSaldoAtual(BigDecimal saldoAtual) {
		this.saldoAtual = saldoAtual;
	}

	public boolean isAtiva() {
		return ativa;
	}

	public TipoConta getTipo() {
		return tipo;
	}

	public void setTipo(TipoConta tipo) {
		this.tipo = tipo;
	}

	//===========================================
	// METODOS AUXILIARES
	//===========================================

	/**
	 * Método responsavel por realizar um lançamento a crédito na movimentação do
	 * banco
	 */
	public void creditar(BigDecimal valor) {

		if (valor.signum() <= 0)
			throw new IllegalArgumentException("o valor de crédito deve ser positivo");
		this.saldoAtual = this.saldoAtual.add(valor);
	}

	public void debitar(BigDecimal valor) {
		if (valor.signum() <= 0)
			throw new IllegalArgumentException("Valor deve ser positivo");
		if (this.saldoAtual.compareTo(valor) < 0)
			throw new IllegalStateException("Saldo insuficiente");
		this.saldoAtual = this.saldoAtual.subtract(valor);
	}
	
	public void ativarConta() {
		if(this.ativa) {
			throw new NegocioException("Esta conta já está ativa");
		}
		
		this.ativa = true;
	}
	
	public void inativar() {
		if(!this.ativa) {
			throw new NegocioException("Esta conta já está inativa");
		}
		
		this.ativa = false;
	}

	// ===========================================
	// HASHCODE E EQUALS
	// ===========================================

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof ContaCorrente))
			return false;
		ContaCorrente that = (ContaCorrente) o;
		return Objects.equals(id, that.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

}
