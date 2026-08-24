package com.dsys.appfood.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

import com.dsys.appfood.config.AutorizacaoUtil;
import com.dsys.appfood.domain.enums.StatusCaixa;
import com.dsys.appfood.domain.enums.TipoMovimentacao;
import com.dsys.appfood.exception.NegocioException;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
  * ENTIDADE: MOVIMENTO DE CAIXA (SESSÃO OPERACIONAL)
 * 
 * CONCEITO: Entidade Operacional (Transaction)
 * 
 * Representa uma SESSÃO de abertura/fechamento de um caixa físico.
 * Cada vez que um operador abre um caixa, um novo SessaoCaixa é criado.
 * 
 * POR QUE EXISTE?
 * - Permite rastrear quem abriu/fechou o caixa
 * - Permite ter histórico de múltiplas sessões do mesmo caixa físico
 * - Separa a operação (dinâmica) do cadastro (estático)
 * 
 * RELACIONAMENTO:
 * - Muitos SessaoCaixa podem pertencer a um CaixaFisico
 * - Cada SessaoCaixa tem um operador responsável
 * 
 * @author David de Sousa
 */
@Entity
@Table(name = "sessao_caixa")
public class SessaoCaixa {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	

	 /**
     * Caixa físico ao qual este movimento pertence
     * 
     * CONCEITO: ManyToOne
     * Um CaixaFisico pode ter múltiplos movimentos (sessões) ao longo do tempo
     */
    @ManyToOne
    @JoinColumn(name = "caixa_id", nullable = false)
    private Caixa caixa;
    
    /**
     * Operador responsável por esta sessão
     */
	@ManyToOne
	@JoinColumn(name = "operador_id")
	private Usuario operador;

	 /**
     * Gerente que autorizou a abertura (se necessário)
     */
	@ManyToOne
	@JoinColumn(name = "gerente_id")
	private Usuario gerente;
	
	/**
     * Data/hora de abertura da sessão
     */
	@Column(name = "data_abertura")
	private LocalDateTime dataAbertura;
	
	/**
     * Data/hora de fechamento da sessão
     * Null se o caixa ainda está aberto
     */
	@Column(name = "data_fechamento")
	private LocalDateTime dataFechamento;
	
	/**
     * Valor inicial do caixa (troco inicial)
     */
	@Column(name = "valor_inicial")
	private BigDecimal valorInicial;
	
	@Column(name = "valor_fechamento")
	private BigDecimal valorFechamento;
	
	@Column(name="saldo")
	private BigDecimal saldo;

	/**
     * Status da sessão (ABERTO ou FECHADO)
     */
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private StatusCaixa status = StatusCaixa.FECHADO;


	//===========================================
	// Construtores
	//===========================================

	public SessaoCaixa() {

	}

	 /**
     * Construtor principal para abertura de caixa
     * 
     * CONCEITO: Construtor com Validação
     * Define valores iniciais automaticamente
     */
	public SessaoCaixa(Caixa caixa, Usuario operador, BigDecimal valorInicial) {

		this.caixa = caixa;
		this.operador = operador;
		this.valorInicial = valorInicial;
		this.saldo = valorInicial;
		this.dataAbertura = LocalDateTime.now();
		this.status = StatusCaixa.ABERTO;
		
	}

	//===========================================
	//Getters e Setters
	//===========================================

	public Integer getId() {
		return id;
	}

	public Caixa getCaixa() {
		return caixa;
	}
	
	public void setCaixa(Caixa caixa) {
		
		this.caixa = caixa;
	}
	
	public Usuario getOperador() {
		return operador;
	}
	
	public void setOperador(Usuario operador) {
		this.operador = operador;
	}
	
	public Usuario getGerente() {
		return gerente;
	}
	
	public void setGerente(Usuario gerente) {
		this.gerente = gerente;
	}
	
	public LocalDateTime getDataAbertura() {
		return dataAbertura;
	}
	
	protected void setDataAbertura(LocalDateTime dataAbertura) {
		this.dataAbertura = dataAbertura;
	}
	
	public LocalDateTime getDataFechamento() {
		return dataFechamento;
	}
	
	protected void setDataFechamento(LocalDateTime dataFechamento) {
		this.dataFechamento = dataFechamento;
	}
	
	public BigDecimal getValorInicial() {
		return valorInicial;
	}
	
	protected void setValorInicial(BigDecimal valorInicial) {
		this.valorInicial = valorInicial;
	}
	
	public BigDecimal getValorFechamento() {
		return valorFechamento;
	}
	
	protected void setValorFechamento(BigDecimal valorFechamento) {
		this.valorFechamento = valorFechamento;
	}
	
	public StatusCaixa getStatus() {
		return status;
	}

	protected void setStatus(StatusCaixa status) {
		this.status = status;
	}

	public BigDecimal getSaldo() {
		return saldo;
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
		if (this == obj) {
			return true;
		}
		if ((obj == null) || (getClass() != obj.getClass())) {
			return false;
		}
		SessaoCaixa other = (SessaoCaixa) obj;
		return Objects.equals(id, other.id);
	}

	//===========================================
	// Métodos Auxiliares
	//===========================================

	/**
	 * Atualiza o saldo atual do caixa baseando-se em uma nova movimentação.
	 * Este método é chamado pela Service após persistir uma movimentação
	 */
	public void atualizarSaldo(BigDecimal valorMovimentacao, TipoMovimentacao tipo ) {
		
		if(this.status.equals(StatusCaixa.FECHADO)) {
	        throw new NegocioException("Não é possível atualizar saldo de um caixa fechado.");
	    }
		
		BigDecimal efeito = switch (tipo) {
		case ENTRADA, ABERTURA -> valorMovimentacao;
		case SAIDA -> valorMovimentacao.negate();
		case FECHAMENTO -> BigDecimal.ZERO;
		};
		
		this.saldo = this.saldo.add(efeito);
	}
	
	/**
     * Fecha a sessão do caixa
     * 
     * CONCEITO: Método de Domínio com Validação
     * 
     * @param gerente Gerente que autorizou o fechamento
     */
    public void fechar(Usuario gerente, BigDecimal valorFinal) {
        if (this.status == StatusCaixa.FECHADO) {
            throw new NegocioException("Este caixa já está fechado.");
        }
             
        AutorizacaoUtil.exigirAutorizacaoGerente(gerente);
        this.valorFechamento = valorFinal;
        this.dataFechamento = LocalDateTime.now();
        this.gerente = gerente;
        this.status = StatusCaixa.FECHADO;
    }
    
    
    /**
     * Calcula a diferença entre o valor contado fisicamente e o saldo
     * esperado pelo sistema, e indica o sentido dela.
     *
     * Por que usar signum() em vez de comparar com == ou < diretamente?
     * BigDecimal é um objeto, não um tipo primitivo — comparar com == 
     * compararia referências de memória, não valores. E diferenca == 0
     * nem compila (BigDecimal não é int). signum() é o jeito idiomático
     * de perguntar "esse número é positivo, negativo ou zero?".
     */
    public BigDecimal calculaDiferenca() {
		return this.valorFechamento.subtract(this.saldo);
	}
    
    public String sentidoDiferenca() {
    	
    	int sinal = calculaDiferenca().signum(); // -1, 0 ou 1 — nunca lança exceção
    	if(sinal > 0) return "SOBRA";
    	if(sinal < 0 )return "FALTA";
    	return "EXATO";
    	
    }
}
