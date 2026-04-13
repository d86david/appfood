package com.dsys.appfood.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.dsys.appfood.domain.enums.StatusPedido;
import com.dsys.appfood.domain.enums.TipoPedido;
import com.dsys.appfood.exception.NegocioException;

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

	@Column(name = "numero_mesa")
	private Integer numeroMesa;

	@Column(name = "obs_pedido")
	private String obsPedido;
	
	@Column(name = "motivo_cancelamento")
	private String motivoCancelamento;

	// ===========================================
	// CONSTRUTORES
	// ===========================================

	public Pedido() {

	}

	public Pedido(Cliente cliente, TipoPedido tipo, String nomeBalcao, BigDecimal valorBruto, BigDecimal desconto,
			Usuario operador, Usuario gerenteAutorizador, Entregador entregador, List<ItemPedido> itens,
			List<Pagamento> pagamentos, String obsPedido) {
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
		this.obsPedido = obsPedido;
		this.dtHoraAbertura = LocalDateTime.now();
		this.status = StatusPedido.PEDIDO_INICIADO;
		alteraStatus(status, operador);
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

	public Integer getNumeroMesa() {
		return numeroMesa;
	}

	public void setNumeroMesa(Integer numeroMesa) {
		this.numeroMesa = numeroMesa;
	}

	public String getObsPedido() {
		return obsPedido;
	}

	public void setObsPedido(String obsPedido) {
		this.obsPedido = obsPedido;
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

	// ===========================================
	// Métodos de Adição
	// ===========================================

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

		Integer numeroPedido = this.getId();
		StatusPedido statusAtual = this.getStatusAtual();
		TipoPedido tipoAtual = this.getTipo();
		Entregador entregador = this.getEntregador();

		// REGRAS GERAIS PARA FINALIZAÇÃO

		// REGRA 1: O pedido não pode ja estar finalizado ou cancelado
		if (statusAtual == StatusPedido.FINALIZADO) {
			throw new NegocioException("O Pedido #" + numeroPedido + "já está finalizado");
		}

		if (statusAtual == StatusPedido.CANCELADO) {
			throw new NegocioException("O Pedido #" + numeroPedido + "está cancelado e não pode ser finalizado.");
		}

		// REGRA 2: O pedido deve estar totalmente pago
		if (!isPago()) {
			throw new NegocioException(String.format("Pedido #/d não pode ser finalizado. Valor restante R$ %.2f",
					numeroPedido, getValorRestante()));
		}

		// REGRA 3: Status deve ser compatível com a finalização
		if (statusAtual != StatusPedido.PRONTO && statusAtual != StatusPedido.SAIU_PARA_ENTREGA) {

			throw new NegocioException(String.format(
					"Pedido #%d está com status '%s'. "
							+ "Para finalizar, o pedido deve estar PRONTO ou SAIU PARA ENTREGA.",
					numeroPedido, statusAtual));
		}

		// REGRAS ESPECÍFICAS POR POR TIPO DE PEDIDO

		// VALIDAÇÃO PARA ENTREGA: Deve ter entregador vinculado
		if (tipoAtual == TipoPedido.ENTREGA) {
			if (entregador == null) {
				throw new NegocioException("Pedido de ENTREGA #" + numeroPedido
						+ " não possui entregador vinculado. Informe um entregador antes de finalizar.");
			}

			// Verifica se o entregador está ativo
			if (!entregador.isAtivo()) {
				throw new NegocioException(
						"O entregador " + entregador.getNome() + " está inativo e não pode finalizar entregas");
			}

		}

		// VALIDAÇÃO PARA MESA: Número da mesa deve estar preenchido
		if (tipoAtual == TipoPedido.MESA && getNumeroMesa() == null) {
			throw new NegocioException("pedido de MESA #" + numeroPedido + " não possui numero de mesa informado.");

		}

		this.dtHoraFinalizacao = LocalDateTime.now();

		alteraStatus(StatusPedido.FINALIZADO, usuario);

	}

	/**
	 * Método para cancelar Pedido
	 */
	public void cancelarPedido(Usuario operador, Usuario gerente, String motivo) {

		Usuario gerAutorizador = gerente;

		// VALIDAÇÕES

		// Verifica se o pedido já está finalizado, se estiver lança exceção
		if (this.getStatus() == StatusPedido.FINALIZADO) {
			throw new NegocioException("Não é possivel cancelar um pedido ja finalizado");
		}

		// Verifica se o pedido ja está cancelado
		if (this.getStatus() == StatusPedido.CANCELADO) {
			throw new NegocioException("O pedido já está cancelado");
		}

		// Se já houve pagamento, exige autorização do gerente
		if (!this.getPagamentos().isEmpty()) {
			if (gerAutorizador == null) {
				throw new NegocioException("Este pedido possui pagamentos registrados. "
						+ "É necessária autorização de um gerente para cancelar.");
			}

			if (!gerAutorizador.isGerente()) {
				throw new NegocioException("Apenas gerentes podem autorizar o cancelamento de pedidos com pagamento.");
			}
		}

		// Valida motivo do cancelamento
		if (motivo == null || motivo.isBlank()) {
			throw new IllegalArgumentException("É obrigatório informar o motivo do cancelamento.");
		}

		// Executa o cancelamento
		alteraStatus(StatusPedido.CANCELADO, operador);
		this.motivoCancelamento = motivo;

		
	}

	/**
	 * 
	 * Método para reabrir um pedido, útil quando um pedido foi cancelado por
	 * engano. Requer autorização de gerente.
	 * 
	 * @param pedido   - pedido que foi cancelado
	 * @param gerente  - Gerente autorizador
	 * @param operador - Operador que está reabrindo
	 */

	public void reabrirPedidoCancelado(Usuario gerente, Usuario operador) {

		// VALIDAÇÕES

		// Valida Gerente
		if (!gerente.isGerente()) {
			throw new NegocioException("Apenas gerentes podem reabrir pedidos cancelados.");
		}

		// Valida se o pedido está mesmo cancelado
		if (this.getStatus() != StatusPedido.CANCELADO) {
			throw new NegocioException(
					"Apenas pedidos CANCELADOS podem ser reabertos. Status atual: " + this.getStatus());
		}

		// ATULIZAÇÕES DE STATUS

		// Determina novo status baseao no que faz sentido
		StatusPedido novoStatus;
		if (this.getItens().isEmpty()) {
			novoStatus = StatusPedido.PEDIDO_INICIADO;
		} else {
			novoStatus = StatusPedido.PENDENTE;
		}

		alteraStatus(novoStatus, operador);
		setGerenteAutorizador(gerente);

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
		return pagamentos.stream().map(Pagamento::getValor).reduce(BigDecimal.ZERO, BigDecimal::add);
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
	 * Método para vincular um entregador e um pedido. Necessário para pedidos de
	 * ENTREGA antes de enviar para entrega.
	 * 
	 * @param pedidoId     - ID do pedido
	 * @param entregadorId - ID do entregador
	 * @param operadorId   - Operador que está vinculando
	 * 
	 */
	public void vincularEntregador(Entregador entregador) {

		// VALIDAÇÕES

		// Verifica se o Tipo do pedido é ENTREGA
		if (this.getTipo() != TipoPedido.ENTREGA) {
			throw new NegocioException("Apenas pedidos do tipo ENTREGA podem ter entregador vinculado.");
		}

		// Verifica se o entregador está ativo
		if (!entregador.isAtivo()) {
			throw new NegocioException("O entregador " + entregador.getNome() + " está inativo.");
		}

		// Verifica se o pedido está pronto
		if (this.getStatus() != StatusPedido.PRONTO) {
			throw new NegocioException("O pedido #" + this.getId() + "não está pronto.");
		}

		// VINCULA O ENTREGADOR AO PEDIDO
		this.entregador = entregador;

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
		Pedido other = (Pedido) obj;
		return Objects.equals(id, other.id);
	}
}
