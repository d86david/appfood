package com.dsys.appfood.domain.model;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * ======================================================================
 * ENTIDADE: IMPRESSORA-CANAL (MAPEAMENTO)
 * ======================================================================
 * 
 * CONCEITO: Tabela de Associação Enriquecida
 * 
 * Esta entidade faz o mapeamento N:N entre Impressora e Canal. Ela responde à
 * pergunta: "Qual impressora imprime qual canal?"
 * 
 * POR QUE UMA ENTIDADE PRÓPRIA (e não @ManyToMany)? - Permite atributos
 * adicionais no relacionamento (ex: 'ativo', 'prioridade') - Permite histórico
 * de mudanças (quem alterou, quando) - Permite validações específicas (ex: não
 * pode ter dois canais iguais para a mesma impressora)
 * 
 * 
 * @author David de Sousa
 */
@Entity
@Table(name = "impressora_canal", uniqueConstraints = @UniqueConstraint(name = "uk_impressora_canal", columnNames = {
		"impressora_id", "canal_id" }))
public class ImpressoraCanal {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	/**
	 * A impressora física que vai receber o conteúdo.
	 * 
	 * CONCEITO: ManyToOne Uma impressora pode estar associada a vários canais.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "impressora_id", nullable = false)
	private Impressora impressora;

	/**
	 * O canal lógico que será impresso.
	 * 
	 * CONCEITO: ManyToOne Um canal pode estar associado a várias impressoras
	 * (backup).
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "canal_id", nullable = false)
	private CanalImpressao canal;

	/**
	 * Flag para ativar/desativar este mapeamento específico.
	 * 
	 * CONCEITO: Granularidade Permite desativar um canal em uma impressora
	 * específica sem afetar os outros canais da mesma impressora.
	 */
	@Column(nullable = false)
	private Boolean ativo = true;

	// ===========================================
	// CONSTRUTORES
	// ===========================================
	public ImpressoraCanal() {
	}

	public ImpressoraCanal(Impressora impressora, CanalImpressao canal) {
		this.impressora = impressora;
		this.canal = canal;
	}

	// ===========================================
	// GETTERS E SETTERS
	// ===========================================
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Impressora getImpressora() {
		return impressora;
	}

	public void setImpressora(Impressora impressora) {
		this.impressora = impressora;
	}

	public CanalImpressao getCanal() {
		return canal;
	}

	public void setCanal(CanalImpressao canal) {
		this.canal = canal;
	}

	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
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
		if (obj == null || getClass() != obj.getClass())
			return false;
		ImpressoraCanal other = (ImpressoraCanal) obj;
		return Objects.equals(id, other.id);
	}
}
