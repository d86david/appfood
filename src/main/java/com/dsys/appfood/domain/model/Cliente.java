package com.dsys.appfood.domain.model;

import java.util.Objects;

import com.dsys.appfood.domain.enums.TipoDocumento;

import jakarta.persistence.*;

@Entity
@Table(name = "cliente")
public class Cliente {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(nullable = false)
	private String nome;

	@Column(name = "telefone_principal")
	private String telefonePrincipal;

	@Column(name = "telefone_secundario")
	private String telefoneSecundario;

	private boolean ativo = true;

	@Column(name = "tipo_documento")
	private TipoDocumento tipoDocumento;

	@Column
	private String documento;

	private String email;

	@Column(name = "observacao_cliente")
	private String observacaoCliente;

	@OneToOne
	@JoinColumn(name = "endereco_id")
	private Endereco endereco;

	// ===========================================
	// CONSTRUTORES
	// ===========================================
	public Cliente() {
	}

	public Cliente(String nome, String telefonePrincipal, String telefoneSecundario, TipoDocumento tipoDocumento,
			String documento, String email, String observacaoCliente, Endereco endereco) {
		this.nome = nome;
		this.telefonePrincipal = telefonePrincipal;
		this.telefoneSecundario = telefoneSecundario;
		this.tipoDocumento = tipoDocumento;
		this.documento = documento;
		this.email = email;
		this.observacaoCliente = observacaoCliente;
		this.endereco = endereco;

	}

	public Cliente(String nome, String telefonePrincipal, String observacaoCliente, Endereco endereco) {
		this.nome = nome;
		this.telefonePrincipal = telefonePrincipal;
		this.observacaoCliente = observacaoCliente;
		this.endereco = endereco;
	}

	// ===========================================
	// GETTERS E SETTERS
	// ===========================================

	public Integer getId() {
		return id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getTelefonePrincipal() {
		return telefonePrincipal;
	}

	public String getTelefoneSecundario() {
		return telefoneSecundario;
	}

	public void setTelefoneSecundario(String telefoneSecundario) {
		this.telefoneSecundario = telefoneSecundario;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public TipoDocumento getTipoDocumento() {
		return tipoDocumento;
	}

	public void setTipoDocumento(TipoDocumento tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}

	public String getDocumento() {
		return documento;
	}

	public void setDocumento(String documento) {
		this.documento = documento;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getObservacaoCliente() {
		return observacaoCliente;
	}

	public void setObservacaoCliente(String observacaoCliente) {
		this.observacaoCliente = observacaoCliente;
	}

	public Endereco getEndereco() {
		return endereco;
	}

	public void setEndereco(Endereco endereco) {
		this.endereco = endereco;
	}

	// ===========================================
	// ALTERAR TELEFONE PRINCIPAL
	// ===========================================
	public void alterarTelefonePrincipal(String telefone) {

		if (telefone.equals(telefoneSecundario)) {
			if (telefone == null || telefone.isBlank()) {
				throw new IllegalArgumentException("Telefone não pode ser vazio.");
			}
			if (telefone.equals(telefoneSecundario)) {
				throw new IllegalArgumentException("O telefone principal deve ser diferente do secundário.");
			}

			this.telefonePrincipal = telefone;
		}
	}

	// ===========================================
	// ATIVAR E DESATIVAR CLIENTE
	// ===========================================
	public void ativar() {
		this.ativo = true;
	}

	public void inativar() {
		this.ativo = false;
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
		Cliente other = (Cliente) obj;
		return Objects.equals(id, other.id);
	}

}
