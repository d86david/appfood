package com.dsys.appfood.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

import com.dsys.appfood.domain.enums.StatusCaixa;
import com.dsys.appfood.domain.enums.TipoMovimentacao;

import jakarta.persistence.*;

/**
 * Entidade que representa o Caixa Pizzaria Esta classe é importante para o
 * registrar o fuxo de entrada e saida das movimentações. - Um Caixa quando
 * fechado não é possivel fazer lançamento - Quando Aberto é nessecário ter um
 * operador. - A Abertura e fechamento depende da autorização do Gerente - Ao
 * abrir um Caixa poderá ser informado um valor de abertura que será creditdo no
 * Caixa. - Ao fecha um caixa o saldo que o caixa estiver no momento do
 * fechamento será debitado e o caixa ficará com saldo zerado
 */
@Entity
@Table(name = "caixa")
public class Caixa {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Enumerated(EnumType.STRING)
	private StatusCaixa status = StatusCaixa.FECHADO;

	private BigDecimal saldo;

	@Column(name = "data_abertura")
	private LocalDateTime dataAbertura;

	@Column(name = "valor_inicial")
	private BigDecimal valorInicial;

	@Column(name = "data_fechamento")
	private LocalDateTime dataFechamento;

	@Column(name = "valor_fechamento")
	private BigDecimal valorFechamento;

	@ManyToOne
	@JoinColumn(name = "operador_id")
	private Usuario operador;

	@ManyToOne
	@JoinColumn(name = "gerente_id")
	private Usuario gerente;

	//===========================================
	// Construtores
	//===========================================
	
	public Caixa() {

	}

	public Caixa(StatusCaixa status, BigDecimal valorInicial, Usuario operador) {

		this.status = status;
		this.valorInicial = valorInicial;
		this.operador = operador;
	}

	//===========================================
	//Getters e Setters
	//===========================================

	public Integer getId() {
		return id;
	}

	public StatusCaixa getStatus() {
		return status;
	}

	public BigDecimal getSaldo() {
		return saldo;
	}

	public LocalDateTime getDataAbertura() {
		return dataAbertura;
	}


	public BigDecimal getValorInicial() {
		return valorInicial;
	}

	public LocalDateTime getDataFechamento() {
		return dataFechamento;
	}

	public BigDecimal getValorFechamento() {
		return valorFechamento;
	}

	public void setValorFechamento(BigDecimal valorFechamento) {
		this.valorFechamento = valorFechamento;
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
		Caixa other = (Caixa) obj;
		return Objects.equals(id, other.id);
	}

	//===========================================
	// Métodos Auxiliares
	//===========================================

	/**
	 * Método responsavel por abrir o Caixa quando o mesmo estiver fechado,
	 * 
	 * @param valorInicial O valor em dinheiro que inicia no fundo de caixa
	 * @param gerente      O gerente que está autorizando a abertura
	 */
	public void abrirCaixa(BigDecimal valorInicial, Usuario gerente) {
		if (status.equals(StatusCaixa.ABERTO)) {
			throw new IllegalArgumentException("O Caixa já está aberto");
		}
		this.gerente = gerente; // Registra o gerente autorizador
		this.valorInicial = valorInicial;
		this.saldo = valorInicial;
		this.status = StatusCaixa.ABERTO;
		this.dataAbertura = LocalDateTime.now();
	}


	/**
	 * Fecha o caixa, limpa o saldo e registra o valor inicial
	 * @param gerente O gerente que valida o fechamento
	 */
	public void fecharCaixa(Usuario gerente) {
		if (status.equals(StatusCaixa.FECHADO)) {
			throw new IllegalArgumentException("O Caixa já está fechado");
		} 
		
		this.gerente = gerente;
		this.valorFechamento = this.saldo;
		this.saldo = BigDecimal.ZERO;
		this.status = StatusCaixa.FECHADO;
		this.dataFechamento = LocalDateTime.now();

	}
	
	/**
	 * Atualiza o saldo atual do caixa baseando-se em uma nova movimentação.
	 * Este método é chamado pela Service após persistir uma movimentação
	 */
	public void atualizarSaldo(BigDecimal valorMovimentacao, TipoMovimentacao tipo ) {
		if(this.status.equals(StatusCaixa.FECHADO)) {
			throw new IllegalStateException("Não é possível atualizar saldo de um caixa fechado.");
		}
		
		if(tipo.equals(TipoMovimentacao.ENTRADA)) {
			this.saldo = this.saldo.add(valorMovimentacao);
		}else {
			this.saldo = this.saldo.subtract(valorMovimentacao);
		}
	}

}
