package com.dsys.appfood.domain.model;

import java.math.BigDecimal;
import java.util.Objects;

import jakarta.persistence.*;


@Entity
@Table(name = "entregador")
public class Entregador {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(nullable = false)
	private String nome; 
	
	private String telefone;
	
	private boolean ativo = true;
	
	@Column(name = "valor_diaria")
	private BigDecimal valorDiaria;
	
	@Column(name = "valor_por_entrega")
	private BigDecimal valorPorEntrega;

	//===========================================
	// CONSTRUTORES
	//===========================================
	
	public Entregador() {
		
	}
	
	public Entregador(String nome, String telefone) {
		this.nome = nome;
		this.telefone = telefone;
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

	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}
	
	public boolean isAtivo() {
		return this.ativo;
	}
	
	public BigDecimal getValorDiaria() {
		return this.valorDiaria;
	}

	public BigDecimal getValorPorEntrega() {
		return valorPorEntrega;
	}
	
	//===========================================
	// DEFINIR VALOR DA ENTREGA
	//===========================================
	
	public void definirValorPorEntrega(BigDecimal valor) {
		
		if(valor == null) {
			this.valorPorEntrega = BigDecimal.ZERO;
			return;
		}
		
		if(valor.signum() == -1) {
			throw new IllegalArgumentException("O Valor por entrega não pode ser negativo");
		}
		
		this.valorPorEntrega = valor;
		
	}
	
	//===========================================
		// DEFINIR VALOR DA DIÁRIA
		//===========================================
		
		public void definirValorDiaria(BigDecimal valor) {
			
			if(valor == null) {
				this.valorDiaria = BigDecimal.ZERO;
				return;
			}
			
			if(valor.signum() == -1) {
				throw new IllegalArgumentException("O valor não pode ser negativo");
			}
			
			this.valorDiaria = valor;
			
		}
	
	//===========================================
	// ATIVAR E INATIVAR
	//===========================================
	
	public void ativar() {
		this.ativo = true;
	}
	
	public void inativar() {
		this.ativo = false;
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
		Entregador other = (Entregador) obj;
		return Objects.equals(id, other.id);
	}
	
}
