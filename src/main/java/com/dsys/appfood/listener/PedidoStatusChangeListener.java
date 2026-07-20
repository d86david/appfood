package com.dsys.appfood.listener;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.dsys.appfood.domain.enums.StatusPedido;
import com.dsys.appfood.event.PedidoStatusChangeEvent;
import com.dsys.appfood.service.ImpressaoService;

@Component
public class PedidoStatusChangeListener {

	private final ImpressaoService impressaoService;

	public PedidoStatusChangeListener (ImpressaoService impressaoService) {

		this.impressaoService = impressaoService;
	}

	/**
	 * Escuta o evento de mudança de status do pedido.
	 * Quando o status muda para PENDENTE, dispara a impressão
	 *
	 * @Async faz com que a execução seja assicrona (não bloqueia o fluxo principal)
	 * @EventListener escuta o evento específico.
	 */
	@Async
	@EventListener
	public void handlePedidoStatusChange(PedidoStatusChangeEvent event) {

		//Só imprime se o novo status for PENDENTE
		if(event.getStatusNovo() == StatusPedido.PENDENTE) {
			try {
				impressaoService.imprimirPedido(event.getPedido().getId());
				System.out.println(">>> [EVENTO] Impressão automática para pedido #" + event.getPedido().getId());
			} catch (Exception e) {
				System.err.println(">>> [EVENTO] Erro ao imprimir pedido #" + event.getPedido().getId() + ": "+ e.getMessage());
				e.printStackTrace();
			}
		}
	}
}
