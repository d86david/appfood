package com.dsys.appfood.domain.model;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * ======================================================================
 * ENTIDADE: CANAL DE IMPRESSÃO (CANAL LÓGICO)
 * ======================================================================
 * 
 * CONCEITO: Abstração do Tipo de Conteúdo
 * 
 * Um Canal representa UM TIPO DE CONTEÚDO que o sistema pode gerar. Ele é
 * independente da impressora física que vai receber esse conteúdo.
 * 
 * POR QUE EXISTE? - Permite rotear o mesmo tipo de conteúdo para impressoras
 * diferentes - Permite adicionar novos tipos de impressão sem mudar código -
 * Separa a lógica de negócio (o que imprimir) da infraestrutura (onde imprimir)
 * 
 * DIFERENÇA ENTRE CANAL E CATEGORIA: - CATEGORIA é um atributo do PRODUTO (ex:
 * "Pizzas", "Bebidas") - CANAL é um destino de IMPRESSÃO (ex: "PIZZAS",
 * "BALCAO")
 * 
 * Uma categoria pode estar associada a um canal, mas são conceitos diferentes.
 * 
 * @author David de Sousa
 */
@Entity
@Table(name = "canal_impressao")
public class CanalImpressao {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	/**
	 * Nome do canal (ex: "BALCAO", "PIZZAS", "LANCHES")
	 * 
	 * CONCEITO: Identificador Único O nome deve ser único porque é usado como chave
	 * de roteamento no ImpressaoService. Se dois canais tivessem o mesmo nome, o
	 * sistema não saberia para onde enviar o conteúdo.
	 */
	@Column(name = "nome", nullable = false, unique = true, length = 50)
	private String nome;

	/**
	 * Descrição amigável do canal (ex: "Cupom do cliente") Usado apenas para
	 * documentação na tela de configuração.
	 */
	@Column(name = "descricao", length = 200)
	private String descricao;

	/**
	 * Flag para ativar/desativar o canal.
	 * 
	 * CONCEITO: Flexibilidade Operacional Se a pizzaria decidir não imprimir mais
	 * na cozinha (ex: todos os pedidos vão para uma tela), você desativa o canal
	 * "PIZZAS" sem precisar excluir a configuração.
	 */
	@Column(name = "ativo", nullable = false)
	private Boolean ativo = true;

	// ===========================================
	// CONSTRUTORES
	// ===========================================
	public CanalImpressao() {
	}

	public CanalImpressao(String nome, String descricao) {
		this.nome = nome;
		this.descricao = descricao;
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

	public Boolean isAtivo() {
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
		CanalImpressao other = (CanalImpressao) obj;
		return Objects.equals(id, other.id);
	}

}
