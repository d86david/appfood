package com.dsys.appfood.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

import com.dsys.appfood.config.AutorizacaoUtil;
import com.dsys.appfood.domain.enums.TipoMovimentacao;

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
 * ENTIDADE: MOVIMENTAÇÃO DE CAIXA (EVENTO)
 * 
 * CONCEITO: Entidade de Evento (Imutável)
 * 
 * Representa cada entrada ou saída individual de valores do Caixa. Esta classe
 * serve como o extrato detalhado para conferência no fechamento.
 * 
 * POR QUE É IMUTÁVEL? - Uma vez registrada, uma movimentação NÃO pode ser
 * alterada ou excluída - Isso garante auditoria e conformidade contábil - Se
 * houver erro, cria-se uma nova movimentação de ajuste (estorno)
 * 
 * 
 * TIPOS DE MOVIMENTAÇÃO: - ABERTURA: Valor inicial do caixa (troco) - ENTRADA:
 * Vendas, aportes - SAIDA: Sangrias, despesas - FECHAMENTO: Saldo final (quando
 * o caixa é fechado)
 * 
 * @author David de Sousa
 */
@Entity
@Table(name = "movimentacao_caixa")
public class MovimentacaoCaixa {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	/**
	 * Movimento de caixa ao qual esta movimentação pertence
	 * 
	 * CONCEITO: ManyToOne Um SessaoCaixa pode ter múltiplas movimentações
	 */
	@ManyToOne
	@JoinColumn(name = "sessao_caixa_id", nullable = false)
	private SessaoCaixa sessaoCaixa;

	/**
	 * ID do pedido relacionado (se for uma venda) Null se for sangria, abertura,
	 * etc.
	 */
	@Column(name = "pedido_id")
	private Integer pedidoId;

	@Enumerated(EnumType.STRING)
	private TipoMovimentacao tipo;

	private BigDecimal valor;

	@Column(length = 500)
	private String descricao;

	@Column(name = "data_hora", nullable = false)
	private LocalDateTime dataHoraMovimento ;

	/**
	 * Origem da movimentação Ex: "CAIXA", "VENDA", "SANGRIA"
	 */
	private String origem;

	/**
	 * Gerente que autorizou a movimentação (se necessário) Null se não requer
	 * autorização (ex: vendas)
	 */
	@ManyToOne
	@JoinColumn(name = "gerente_id")
	private Usuario gerente;

	private Boolean estornada = false; 
	
	/**
	 * Referência para a movimentação original, quando este registro é um ESTORNO de
	 * outra movimentação.
	 *
	 * Por que auto-relacionamento (self-reference)? MovimentacaoCaixa é imutável —
	 * nunca alteramos nem apagamos um registro existente. Para corrigir um erro,
	 * criamos um NOVO registro (com efeito oposto) e o ligamos ao original através
	 * deste campo, preservando o histórico completo para auditoria.
	 *
	 * Fica null para movimentações "normais" (venda, sangria, abertura que não são,
	 * elas mesmas, um estorno).
	 */
	@ManyToOne
	@JoinColumn(name = "estorno_de_id")
	private MovimentacaoCaixa estornoDe;

	// ===========================================
	// CONSTRUTORES
	// ===========================================
	public MovimentacaoCaixa() {

	}

	// ===========================================
	// GETTERS E SETTERS
	// ===========================================

	public Integer getId() {
		return id;
	}

	public SessaoCaixa getSessaoCaixa() {
		return sessaoCaixa;
	}

	private void setSessaoCaixa(SessaoCaixa sessaoCaixa) {
		this.sessaoCaixa = sessaoCaixa;
	}

	public Integer getPedidoId() {
		return pedidoId;
	}

	private void setPedidoId(Integer pedidoId) {
		this.pedidoId = pedidoId;
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

	private void setGerente(Usuario gerente) {

		this.gerente = gerente;
	}

	public Usuario getGerente() {
		return gerente;
	}
	

	protected MovimentacaoCaixa getEstornoDe() {
		return estornoDe;
	}

	public Boolean isEstornada() {
		return estornada;
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
		if (this == obj) {
			return true;
		}
		if ((obj == null) || (getClass() != obj.getClass())) {
			return false;
		}
		MovimentacaoCaixa other = (MovimentacaoCaixa) obj;
		return Objects.equals(id, other.id);
	}

	// =====================================================
	// --- METODOS DE FÁBRICA (STATIC FACTORY METHODS) ---
	// =====================================================

	/**
	 * Cria uma movimentação de ABERTURA do caixa
	 * 
	 * CONCEITO: Static Factory Method Centraliza a criação de objetos com
	 * validações específicas
	 * 
	 * @param sessaoCaixa  Movimento de caixa ao qual pertence
	 * @param valorInicial Valor inicial (troco)
	 * @return MovimentacaoCaixa criada
	 */
	public static MovimentacaoCaixa criarAberturaCaixa(SessaoCaixa sessaoCaixa) {

		MovimentacaoCaixa mov = new MovimentacaoCaixa();
		mov.setSessaoCaixa(sessaoCaixa);
		mov.setTipo(TipoMovimentacao.ABERTURA);
		mov.setOrigem("ABERTURA");
		mov.setValor(sessaoCaixa.getValorInicial());
		mov.dataHoraMovimento = sessaoCaixa.getDataAbertura();
		mov.setDescricao("Abertura de Caixa");
		
		return mov;
	}

	/**
	 * Cria uma movimentação de entrada referente a uma venda concluída.
	 */
	public static MovimentacaoCaixa criarEntradaCaixa(SessaoCaixa sessaoCaixa, BigDecimal valor, Integer pedidoId) {
		MovimentacaoCaixa mov = new MovimentacaoCaixa();
		mov.setSessaoCaixa(sessaoCaixa);
		mov.setTipo(TipoMovimentacao.ENTRADA);
		mov.setOrigem("CAIXA");
		mov.setValor(valor);
		mov.setPedidoId(pedidoId);
		mov.setDescricao("Venda realizada - Pedido #" + pedidoId);
		sessaoCaixa.atualizarSaldo(valor, mov.getTipo());
		// Entradas de venda geralmente não precisam de gerente, pois o operador está
		// logado.

		return mov;
	}

	/**
	 * Cria uma movimentação de saída para retirada do dinheiro (sangria)
	 * 
	 * CONCEITO: Validação no Factory Method Garante que sangrias tenham autorização
	 * de gerente
	 * 
	 * @param sessaoCaixa Movimento de caixa ao qual pertence
	 * @param valor       Valor da sangria
	 * @param gerente     Gerente que autorizou
	 * @param motivo      Motivo da sangria
	 * @return MovimentacaoCaixa criada
	 */
	public static MovimentacaoCaixa criarSaidaSangria(SessaoCaixa sessaoCaixa, BigDecimal valor, Usuario gerente,
			String motivo) {

		if (valor.compareTo(BigDecimal.ZERO) <= 0) {
			throw new IllegalArgumentException("O valor da sangria deve ser maior que zero!");
		}

		AutorizacaoUtil.exigirAutorizacaoGerente(gerente);
		MovimentacaoCaixa mov = new MovimentacaoCaixa();
		mov.setSessaoCaixa(sessaoCaixa);
		mov.setTipo(TipoMovimentacao.SAIDA);
		mov.setValor(valor);
		mov.setGerente(gerente);
		mov.setDescricao(motivo);

		sessaoCaixa.atualizarSaldo(valor, mov.getTipo());
		return mov;
	}

	/**
	 * Cria uma movimentação de Estorno de uma movimentação
	 * 
	 * NÃO valida se o saldo é suficiente para cobrir o estorno, ao contrário
	 * de criarSaidaSangria.
	 *
	 * Por quê? Sangria é uma retirada física imediata — não pode exceder o
	 * que está fisicamente na gaveta. Estorno é uma correção retroativa do
	 * livro-razão — pode deixar o saldo negativo, o que é uma informação
	 * válida (sinaliza que o gerente precisa repor dinheiro na sessão por
	 * fora do sistema, seja de outro caixa ou de reserva). O saldo negativo
	 * aqui não é um erro a ser evitado, é um sinal a ser lido.
	 */
	public static MovimentacaoCaixa criarEstorno(MovimentacaoCaixa original, Usuario gerente, String motivo) {

		if (original.isEstornada()) {
			throw new IllegalArgumentException("Não é possivel estornar mais de uma vez o mesmo lançamento!");
		}
		
		if (original.getTipo() == TipoMovimentacao.ABERTURA) {
			throw new IllegalArgumentException("Não é possivel estornar lançamento de Abertura!");
		}


		AutorizacaoUtil.exigirAutorizacaoGerente(gerente);
		MovimentacaoCaixa estorno = new MovimentacaoCaixa();
		estorno.setSessaoCaixa(original.sessaoCaixa);

		if (original.getTipo() == TipoMovimentacao.ENTRADA || original.getTipo() == TipoMovimentacao.ABERTURA) {
			estorno.setTipo(TipoMovimentacao.SAIDA);
		} else {
			estorno.setTipo(TipoMovimentacao.ENTRADA);
		}

		original.estornada = true;
		estorno.estornoDe = original;

		estorno.setGerente(gerente);
		estorno.setValor(original.getValor());
		estorno.setDescricao("ESTORNO: " + motivo);
		estorno.sessaoCaixa.atualizarSaldo(estorno.getValor(), estorno.getTipo());

		return estorno;
	}

	/**
	 * Cria uma movimentação de FECHAMENTO do caixa,
	 * 
	 * @param sessaoCaixa Movimento de caixa ao qual pertence
	 * @param saldoFinal  Saldo final do caixa
	 * @return MovimentacaoCaixa criada
	 */
	public static MovimentacaoCaixa criarFechamento(SessaoCaixa sessaoCaixa, BigDecimal valorFechamento,
			Usuario gerente) {

		MovimentacaoCaixa mov = new MovimentacaoCaixa();
		mov.setSessaoCaixa(sessaoCaixa);
		mov.setGerente(gerente);
		mov.setTipo(TipoMovimentacao.FECHAMENTO);
		mov.setOrigem("FECHAMENTO");
		mov.setValor(valorFechamento);
		mov.setDescricao("Fechamento do caixa - Saldo final : " + sessaoCaixa.getSaldo());

		sessaoCaixa.fechar(gerente, valorFechamento);
		mov.dataHoraMovimento = sessaoCaixa.getDataFechamento();

		return mov;
	}

	// ===========================================
	// Metodos Auxiliares
	// ===========================================

	/**
	 * Métodos Auxiliar para formatar a descrição da movimentação para relatórios
	 */
	public String getResumoFormatado() {
		return String.format("[%s] - %s - R$ %.2f", this.tipo, this.descricao, this.valor);
	}

	/**
	 * Método auxiliar para verificar se a movimentação exigiu autorizaçção de nivel
	 * superior.
	 */
	public boolean isAutorizadaPorGerente() {
		return this.gerente != null;
	}

}
