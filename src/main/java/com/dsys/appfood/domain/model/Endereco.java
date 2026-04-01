package com.dsys.appfood.domain.model;

import java.util.Objects;

import jakarta.persistence.*;

@Entity
@Table(name = "endereco")
public class Endereco {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(nullable = false)
	private String logradouro;

	private String numero;

	private String complemento;

	@Column(nullable = false)
	private String bairro;

	private String cidade;

	private String uf;
	
	private String cep;
	
	@Column(name = "ponto_referencia")
	private String pontoReferencia;

	// ===========================================
	// CONSTRUTORES
	// ===========================================

	public Endereco() {

	}

	public Endereco(String logradouro, String numero, String complemento, String bairro, String cidade,
			String uf, String cep, String pontoReferencia) {
		this.logradouro = logradouro;
		this.numero = numero;
		this.complemento = complemento;
		this.bairro = bairro;
		this.cidade = cidade;
		this.uf = uf;
		this.cep = cep;
		this.pontoReferencia = pontoReferencia;
	}

	// ===========================================
	// GETTERS E SETTERS
	// ===========================================

	public Integer getId() {
		return id;
	}

	public String getLogradouro() {
		return logradouro;
	}

	public void setLogradouro(String logradouro) {
		this.logradouro = logradouro;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}
	
	public String getComplemento() {
		return complemento;
	}
	
	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}

	public String getBairro() {
		return bairro;
	}

	public void setBairro(String bairro) {
		this.bairro = bairro;
	}

	public String getCidade() {
		return cidade;
	}

	public void setCidade(String cidade) {
		this.cidade = cidade;
	}

	public String getUf() {
		return uf;
	}

	public void setUf(String uf) {
		this.uf = uf;
	}
	
	public String getCep() {
		return cep;
	}

	public void setCep(String cep) {
		this.cep = cep;
	}
	
	public String getPontoReferencia() {
		return pontoReferencia;
	}

	public void setPontoReferencia(String pontoReferencia) {
		this.pontoReferencia = pontoReferencia;
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
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Endereco other = (Endereco) obj;
		return Objects.equals(id, other.id);
	}
}
