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
 * ENTIDADE: IMPRESSORA (DISPOSITIVO FÍSICO)
 * ======================================================================
 * 
 * CONCEITO: Separação entre Hardware e Lógica
 * 
 * Esta entidade representa a IMPRESSORA FÍSICA em si — o aparelho que é
 * conectado na tomada e que imprime o papel.
 * 
 * POR QUE EXISTE? - Permite configurar cada impressora individualmente
 * (largura, modelo, porta) - Permite ter múltiplas impressoras no mesmo
 * estabelecimento - Permite substituir uma impressora quebrada sem perder o
 * histórico
 * 
 * @author David de Sousa
 */
@Entity
@Table(name = "impressora")
public class Impressora {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	/**
	 * Nome amigável da impressora (ex: "Impressora do Balcão") Usado em relatórios
	 * e na tela de configuração.
	 */
	@Column(name = "nome", nullable = false, length = 100)
	String nome;

	/**
	 * Largura da bobina de papel em colunas de caracteres. Valores comuns: 32
	 * (58mm), 48 (80mm), 56 (80mm alta densidade)
	 * 
	 * CONCEITO: Configuração por Dispositivo Cada impressora pode ter uma largura
	 * diferente, então esse atributo pertence à Impressora, não ao Canal.
	 */
	@Column(name = "largura_coluna", nullable = false)
	private Integer larguraColunas;

	/**
	 * Modelo/fabricante da impressora (ex: "Epson TM-T20", "Bematech") Útil para
	 * documentação e suporte técnico.
	 */
	@Column(name = "modelo", length = 100)
	private String modelo;

	/**
	 * Porta de comunicação para conexão com a impressora. Exemplos: "USB001",
	 * "COM3", "192.168.1.100:9100"
	 * 
	 */
	@Column(name = "porta_comuicacao", length = 100)
	private String portaComunicacao;

	/**
	 * Flag para ativar/desativar a impressora sem excluí-la.
	 * 
	 * CONCEITO: Soft Delete / Inativação Lógica Se uma impressora quebrar, você a
	 * desativa em vez de excluí-la. Isso mantém o histórico de configurações e
	 * evita erros de integridade referencial (foreign keys).
	 */
	@Column(nullable = false)
	private Boolean ativa = true;

	// ===========================================
	// CONSTRUTORES
	// ===========================================
	public Impressora() {
	}

	public Impressora(String nome, Integer larguraColunas) {
		this.nome = nome;
		this.larguraColunas = larguraColunas;
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

	public Integer getLarguraColunas() {
		return larguraColunas;
	}

	public void setLarguraColunas(Integer larguraColunas) {
		this.larguraColunas = larguraColunas;
	}

	public String getModelo() {
		return modelo;
	}

	public void setModelo(String modelo) {
		this.modelo = modelo;
	}

	public String getPortaComunicacao() {
		return portaComunicacao;
	}

	public void setPortaComunicacao(String portaComunicacao) {
		this.portaComunicacao = portaComunicacao;
	}

	public Boolean isAtiva() {
		return ativa;
	}

	public void setAtiva(Boolean ativa) {
		this.ativa = ativa;
	}
	
    // ===========================================
    // HASHCODE E EQUALS
    // ===========================================
    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Impressora other = (Impressora) obj;
        return Objects.equals(id, other.id);
    }
}
