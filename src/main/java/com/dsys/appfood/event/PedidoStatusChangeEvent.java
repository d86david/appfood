package com.dsys.appfood.event;

import org.springframework.context.ApplicationEvent;

import com.dsys.appfood.domain.enums.StatusPedido;
import com.dsys.appfood.domain.model.Pedido;

public class PedidoStatusChangeEvent extends ApplicationEvent {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private final Pedido pedido;
	private final StatusPedido statusAnterior;
	private final StatusPedido statusNovo;

	public PedidoStatusChangeEvent(Object source, Pedido pedido, StatusPedido statusAnterior, StatusPedido statusNovo){
		super(source);
		this.pedido = pedido;
		this.statusAnterior = statusAnterior;
		this.statusNovo = statusNovo;
	}

	public Pedido getPedido() {
		return pedido;
	}

	public StatusPedido getStatusAnterior() {
		return statusAnterior;
	}

	public StatusPedido getStatusNovo() {
		return statusNovo;
	}
}
