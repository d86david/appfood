package com.dsys.appfood.domain.model;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * ENTIDADE: CAIXA FÍSICO (CADASTRO)
 * 
 * Entidade de Domínio que Representa o CAIXA FÍSICO em si (Master Data)
 * 
 *  O equipamento/gaveta que existe
 * independentemente de estar aberto ou fechado.
 * 
 * - Permite ter múltiplos caixas físicos n
 * - Cada caixa físico pode ter múltiplas sessões de abertura/fechamento
 * - Separa o cadastro (estático) da operação (dinâmico)
 * 
 * @author David de Sousa
 */
@Entity
@Table(name = "caixa")
public class Caixa {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	/**
     * Nome/identificação do caixa físico
     * Ex: "Caixa 01", "Caixa Principal", "Caixa Delivery"
     * 
     * - Unique Constraint
     * Garante que não haja dois caixas com o mesmo nome
     */
	@Column( nullable = false, unique = true, length = 50)
	private String nome;
	
	/**
     * Descrição opcional do caixa
     * Ex: "Caixa principal do balcão", "Caixa exclusivo para entregas"
     */
	@Column(length = 200)
	private String descricao;
	
	/**
     * Localização física do caixa
     * Ex: "Balcão", "Delivery", "Reserva"
     */
	@Column(length = 100)
	private String localizacao;
	
	 /**
     * Flag para ativar/desativar o caixa físico
     * 
     * CONCEITO: Soft Delete (Inativação Lógica)
     * Se um caixa físico quebrar ou for substituído, você o desativa
     * em vez de excluí-lo. Isso mantém o histórico de movimentos.
     * 
     * POR QUE NÃO EXCLUIR?
     * - Preserva o histórico de movimentações
     * - Evita erros de integridade referencial (foreign keys)
     * - Permite reativação futura se necessário
     */
    @Column(nullable = false)
	private Boolean ativo;
	
    // ===========================================
    // CONSTRUTORES
    // ===========================================
    public Caixa() {}
    
    public Caixa(String nome, String descricao, String localizacao) {
        this.nome = nome;
        this.descricao = descricao;
        this.localizacao = localizacao;
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

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getLocalizacao() {
		return localizacao;
	}

	public void setLocalizacao(String localizacao) {
		this.localizacao = localizacao;
	}
	
	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	public Boolean isAtivo() {
		return ativo;
	}
    
	 // ===========================================
    // MÉTODOS AUXILIARES
    // ===========================================
    
    /**
     * Ativa o caixa físico
     */
    public void ativar() {
        this.ativo = true;
    }
    
    /**
     * Inativa o caixa físico
     */
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
		Caixa other = (Caixa) obj;
		return Objects.equals(id, other.id);
	}
}
