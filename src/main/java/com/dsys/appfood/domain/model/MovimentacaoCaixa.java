package com.dsys.appfood.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

import com.dsys.appfood.domain.enums.TipoMovimentacao;

import jakarta.persistence.*;

/**
 * Representa cada entrada ou saída individual de valores do Caixa.
 * Esta classe serve como o extrato detalhado para conferência no fechamento.
 */
@Entity
@Table(name = "movimentacao_caixa")
public class MovimentacaoCaixa {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@ManyToOne
	@JoinColumn(name = "caixa_id")
	private Caixa caixa;
	
	@Enumerated(EnumType.STRING)
	private TipoMovimentacao tipo;
	
	private BigDecimal valor;
	
	private String descricao;
	
	@Column(name = "data_hora")
	private LocalDateTime dataHoraMovimento = LocalDateTime.now();
	
	private String origem;
	
	private Usuario gerente;
	
	//===========================================
	// GETTERS E SETTERS
	//===========================================
	
	public Integer getId() {
		return id;
	}

	public Caixa getCaixa() {
		return caixa;
	}

	private void setCaixa(Caixa caixa) {
		this.caixa = caixa;
	}

	public TipoMovimentacao getTipo() {
		return tipo;
	}

	private void setTipo(TipoMovimentacao tipo) {
		this.tipo = tipo;
	}

	public BigDecimal getValor() {
		return valor;
	}

	private void setValor(BigDecimal valor) {
		this.valor = valor;
	}

	public String getDescricao() {
		return descricao;
	}

	private void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public LocalDateTime getDataHoraMovimento() {
		return dataHoraMovimento;
	}


	public String getOrigem() {
		return origem;
	}

	private void setOrigem(String origem) {
		this.origem = origem;
	}

	public Usuario getGerente() {
		return gerente;
	}

	private void setGerente(Usuario gerente) {
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
		MovimentacaoCaixa other = (MovimentacaoCaixa) obj;
		return Objects.equals(id, other.id);
	}

	//=====================================================
	// --- METODOS DE FÁBRICA (STATIC FACTORY METHODS) ---
	//=====================================================
	/**
	 * Cria uma movimentação de entrada referente a uma venda concluída.
	 */
	public static MovimentacaoCaixa criarEntradaCaixa(Caixa caixa, BigDecimal valor, Integer pedidoId ) {
		MovimentacaoCaixa mov = new MovimentacaoCaixa();
		mov.setCaixa(caixa);
		mov.setTipo(TipoMovimentacao.ENTRADA);
		mov.setOrigem("CAIXA");
		mov.setValor(valor);
		mov.setDescricao("Venda realizada - Pedido #" + pedidoId);
		//Entradas de venda geralmente não precisam de gerente, pois o operador está logado.
		
		return mov;
	}
	
	/**
	 * Cria uma movimentação  de saída para retirada do dinheiro.
	 * Exige um gerente autorizador e uma justificativa.
	 */
	public static MovimentacaoCaixa criarSaidaSangria(Caixa caixa, BigDecimal valor, Usuario gerente, String motivo) {
		if(gerente == null) {
			throw new IllegalArgumentException("Sangrias exigem autorização de um gerente!");
		}
		if(valor.compareTo(BigDecimal.ZERO) <= 0) {
			throw new IllegalArgumentException("O valor da sangria deve ser maior que zero!");
		}
		
		MovimentacaoCaixa mov = new MovimentacaoCaixa();
		mov.setCaixa(caixa);
		mov.setTipo(TipoMovimentacao.SAIDA);
		mov.setValor(valor);
		mov.setGerente(gerente);
		mov.setDescricao("Sangria: " + motivo);
		return mov;
	}
	
	//===========================================
	//Metodos Auxiliares
	//===========================================
	
	/**
	 * Métodos Auxiliar para formatar a descrição da movimentação para relatórios
	 */
	public String getResumoFormatado() {
		return String.format("[%s] - %s - R$ %.2f", 
				this.tipo, this.descricao, this.valor);
	}
	
	/**
	 *  Método auxiliar para verificar se a movimentação exigiu autorizaçção de nivel superior. 
	 */
	public boolean isAutorizadaPorGerente() {
		return this.gerente != null;
	}

}
