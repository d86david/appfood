package com.dsys.appfood.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.dsys.appfood.domain.enums.StatusPedido;
import com.dsys.appfood.domain.enums.TipoPedido;
import jakarta.persistence.*;

@Entity
@Table(name = "pedido")
public class Pedido {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "cliente_id")
	private Cliente cliente;

	@Enumerated(EnumType.STRING)
	private TipoPedido tipo;

	@Column(name = "nome_balcao")
	private String nomeBalcao;

	@Column(name = "dt_hora_abertura")
	private LocalDateTime dtHoraAbertura;

	@Column(name = "dt_hora_finalizacao")
	private LocalDateTime dtHoraFinalizacao;

	@Column(name = "valor_bruto")
	private BigDecimal valorBruto;

	@Column(name = "valor_desconto")
	private BigDecimal desconto;

	@Column(name = "taxa_entrega")
	private BigDecimal taxaEntrega;

	@Column(name = "total_total")
	private BigDecimal valorTotal;

	@Enumerated(EnumType.STRING)
	private StatusPedido status;

	private boolean pedidoPago;

	@OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL)
	private List<StatusPedidoHistorico> historicoStatus = new ArrayList<>();

	@OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL)
	private List<Pagamento> pagamentos = new ArrayList<>();

	@ManyToOne
	@JoinColumn(name = "operador_id")
	private Usuario operador;

	@ManyToOne
	@JoinColumn(name = "gerente_autorizador_id")
	private Usuario gerenteAutorizador;

	@ManyToOne
	@JoinColumn(name = "entregador_id")
	private Entregador entregador;

	// Relacionamento Um-para_Muitos com os itens
	// O 'mappedBy' diz que o mapeamento é controlado pelo campo 'pedido' na classe
	// ItemPedido

	@OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ItemPedido> itens = new ArrayList<>();

	//===========================================
	// CONSTRUTORES
	//===========================================

	public Pedido() {

	}

	public Pedido(Cliente cliente, TipoPedido tipo, String nomeBalcao, BigDecimal valorBruto, BigDecimal desconto,
			Usuario operador, Usuario gerenteAutorizador, Entregador entregador, List<ItemPedido> itens,
			List<Pagamento> pagamentos) {
		this.cliente = cliente;
		this.tipo = tipo;
		this.nomeBalcao = nomeBalcao;
		this.valorBruto = valorBruto;
		this.desconto = desconto;
		this.operador = operador;
		this.gerenteAutorizador = gerenteAutorizador;
		this.entregador = entregador;
		this.itens = itens;
		this.pagamentos = pagamentos;
		this.dtHoraAbertura = LocalDateTime.now();
		this.status = StatusPedido.PEDIDO_INICIADO;
		alteraStatus(status, operador);
	}

	//===========================================
	// GETTERS E SETTERS
	//===========================================

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public TipoPedido getTipo() {
		return tipo;
	}

	public void setTipo(TipoPedido tipo) {
		this.tipo = tipo;
	}

	public StatusPedido getStatus() {
		return status;
	}

	public String getNomeBalcao() {
		return nomeBalcao;
	}

	public void setNomeBalcao(String nomeBalcao) {
		this.nomeBalcao = nomeBalcao;
	}

	public LocalDateTime getDtHoraAbertura() {
		return dtHoraAbertura;
	}

	public LocalDateTime getDtHoraFinalizacao() {
		return dtHoraFinalizacao;
	}

	public BigDecimal getValorBruto() {
		return valorBruto;
	}

	public void setValorBruto(BigDecimal valorBruto) {
		this.valorBruto = valorBruto;
	}

	public BigDecimal getDesconto() {
		return desconto;
	}

	public void setDesconto(BigDecimal desconto) {
		this.desconto = desconto;
	}

	public Usuario getOperador() {
		return operador;
	}

	public void setOperador(Usuario operador) {
		this.operador = operador;
	}

	public Usuario getGerenteAutorizador() {
		return gerenteAutorizador;
	}

	public void setGerenteAutorizador(Usuario gerenteAutorizador) {
		this.gerenteAutorizador = gerenteAutorizador;
	}

	public Entregador getEntregador() {
		return entregador;
	}

	public void setEntregador(Entregador entregador) {
		this.entregador = entregador;
	}

	public List<ItemPedido> getItens() {
		return itens;
	}

	public List<Pagamento> getPagamentos() {
		return pagamentos;
	}

	public BigDecimal getTaxaEntrega() {
		return taxaEntrega;
	}

	public void setTaxaEntrega(BigDecimal taxaEntrega) {
		this.taxaEntrega = taxaEntrega;
	}

	public List<StatusPedidoHistorico> getHistoricoStatus() {
		return historicoStatus;
	}

	public boolean getPedidoPago() {
		return pedidoPago;
	}

	/**
	 * Método que calcula o valor liquido do pedido, esse ainda não é o valor Total
	 * do pedido se o mesmo for delivery.
	 * 
	 * @return o valor Bruto - descontos
	 */
	public BigDecimal getValorLiquido() {
		BigDecimal desc = desconto == null ? BigDecimal.ZERO : desconto;

		return valorBruto.subtract(desc);
	}

	/**
	 * Método responsavel por calcular e armazenas o valor total do pedido.
	 * 
	 * @return o valor total do pedido
	 */
	public BigDecimal calcularTotal() {
		BigDecimal taxa = taxaEntrega == null ? BigDecimal.ZERO : taxaEntrega;

		valorTotal = getValorLiquido().add(taxa);

		return valorTotal;
	}

	/**
	 * Metodo para saber o Status do atual do pedido.
	 * 
	 * @return o ultimo da lista
	 */
	public StatusPedido getStatusAtual() {
		if (historicoStatus.isEmpty()) {
			return null;
		}

		StatusPedidoHistorico ultimo = historicoStatus.get(historicoStatus.size() - 1);

		return ultimo.getStatus();
	}

	/**
	 * Método para alterar o Status do Pedido, e gravar o histórico
	 * 
	 * @param novoStatus
	 * @param usuario
	 */
	public void alteraStatus(StatusPedido novoStatus, Usuario usuario) {

		this.status = novoStatus;

		StatusPedidoHistorico historico = new StatusPedidoHistorico();

		historico.setStatus(novoStatus);
		historico.setDataHora(LocalDateTime.now());
		historico.setPedido(this);
		historico.setUsuario(usuario);

		historicoStatus.add(historico);
	}

	//===========================================
	// Métodos de Adição 
	//===========================================

	/**
	 * Método responsavel por adicionar um item ao pedido.
	 * 
	 * @param item
	 */
	public void adicionarItem(ItemPedido item) {
		item.associarPedido(this);

		itens.add(item);
	}

	/**
	 * Método responsavel por adicionar um pagamento ao pedido
	 * 
	 * @param pagamento
	 */
	public void registrarPagamento(Pagamento pagamento) {

		Objects.requireNonNull(pagamento, "O pagamento não pode ser nulo para processar o pedido.");

		if (isPago()) {
			throw new IllegalStateException("O pedido já está totalmente pago.");
		}

		pagamento.associarPedido(this);

		pagamentos.add(pagamento);

		if (isPago()) {
			this.pedidoPago = true;
		}

	}

	/**
	 * Método responsavel por adicionar um histórico ao hitórico de status do pedido
	 * 
	 * @param historico
	 */
	public void adicionarHistorico(StatusPedidoHistorico historico) {
		historico.setPedido(this);

		historicoStatus.add(historico);
	}

	/**
	 * Método responsavel por Finalizar um pedido, o pedido pode ser finalizado
	 * quando ja estiver concludo pela cozinha ou quando o entregador já estiver
	 * levado para entrega
	 * 
	 * @param valorPago
	 */
	public void finalizarPedido(Usuario usuario) {

		if (!isPago()) {
			throw new IllegalStateException("O pedido ainda não foi pago");
		}

		StatusPedido statusAtual = getStatusAtual();

		if (statusAtual != StatusPedido.PRONTO && statusAtual != StatusPedido.SAIU_PARA_ENTREGA) {

			throw new IllegalStateException("Pedido não está em um status que permita finalização.");
		}

		this.dtHoraFinalizacao = LocalDateTime.now();

		alteraStatus(StatusPedido.FINALIZADO, usuario);

	}

	/**
	 * Métodopara cancelar Pedido
	 */
	public void cancelarPedido() {
		if (getStatusAtual() == StatusPedido.FINALIZADO) {
			throw new IllegalStateException("O pedido já está finalizado");
		}
		alteraStatus(StatusPedido.CANCELADO, operador);
	}

	/**
	 * Método para retornar se o pedido está pago ou não
	 * 
	 * @return
	 */
	public boolean isPago() {
		BigDecimal totalPago = getTotalPago();
		return totalPago.compareTo(calcularTotal()) >= 0;
	}

	/**
	 * Método para somar todos os pagamentos
	 * 
	 * @return o valor dos pagamentos somados
	 */
	public BigDecimal getTotalPago() {
		return pagamentos.stream()
				.map(Pagamento::getValor)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	/**
	 * Define o troco apenas se a forma de pagamento for DINHEIRO. O cálculo é feito
	 * automaticamente: Valor Pago - Valor do Pedido.
	 */
	public BigDecimal calcularTroco() {
		BigDecimal totalPago = getTotalPago();
		BigDecimal totalPedido = calcularTotal();

		if (totalPago.compareTo(totalPedido) > 0) {
			return totalPago.subtract(totalPedido);
		}

		return BigDecimal.ZERO;
	}

	/**
	 * Método que sabe quanto falta Pagar
	 * 
	 * @return
	 */
	public BigDecimal getValorRestante() {

		BigDecimal restante = calcularTotal().subtract(getTotalPago());

		if (restante.signum() < 0) {
			return BigDecimal.ZERO;
		}

		return restante;
	}

	//===========================================
	//HASHCODE E EQUALS
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
		Pedido other = (Pedido) obj;
		return Objects.equals(id, other.id);
	}
}
