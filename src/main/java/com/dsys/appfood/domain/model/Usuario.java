package com.dsys.appfood.domain.model;

import java.util.Objects;

import com.dsys.appfood.domain.enums.TipoUsuario;

import jakarta.persistence.*;

@Entity
@Table(name = "usuario")
public class Usuario {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(nullable = false)
	private String nome;
	
	@Column(nullable = false)
	private String login;
	
	@Column(nullable = false)
	private String senha;
	
	private boolean ativo = true;
	
	@Column(nullable = false)
	private String telefone;
	
	@Enumerated(EnumType.STRING)
	private TipoUsuario tipo;

	//=====================================
	// CONSTRUTORES
	//=====================================
	
	public Usuario() {
		
	}

	public Usuario(Integer id, String nome, String login, String senha, String telefone, TipoUsuario tipo) {
		super();
		this.id = id;
		this.nome = nome;
		this.login = login;
		this.senha = senha;
		this.telefone = telefone;
		this.tipo = tipo;
	}

	//=====================================
	//GETTERS E SETTERS
	//=====================================

	public Integer getId() {
		return id;
	}


	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}
	
	public boolean isAtivo() {
		return this.ativo;
	}

	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	public TipoUsuario getTipo() {
		return tipo;
	}

	public void setTipo(TipoUsuario tipo) {
		this.tipo = tipo;
	}
	
	/**
	 * Verifica se este usuário tem perfil de gerência.
	 * Gerentes e Admins podem autorizar operações restritas.
	 */
	public boolean isGerente() {
		return this.tipo == TipoUsuario.GERENTE 
				|| this.tipo == TipoUsuario.ADM;
	}
	
	/**
	 * Verifica se este usuário é um operador de caixa.
	 */
	public boolean isOperador() {
	    return this.tipo == TipoUsuario.OPERADOR;
	}
	
	/**
	 * Ativa Usuario
	 */
	public void ativar() {
		this.ativo = true;
	}
	
	/**
	 * Inativa usuario
	 */
	public void inativar() {
		this.ativo = false;
	}
	
	//=====================================
	// HASHCODE E EQUALS
	//=====================================

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
		Usuario other = (Usuario) obj;
		return Objects.equals(id, other.id);
	}

}
