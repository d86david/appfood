package com.dsys.appfood.service;

import com.dsys.appfood.domain.enums.StatusPedido;
import com.dsys.appfood.domain.model.Pedido;
import com.dsys.appfood.domain.model.StatusPedidoHistorico;
import com.dsys.appfood.exception.PedidoNaoEncontradoException;
import com.dsys.appfood.repository.PedidoRepository;
import com.dsys.appfood.repository.StatusPedidoHistoricoRepository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Serviço para consulta do histórico de status dos pedidos.
 * 
 * Responsabilidade ÚNICA: Serviço de "consulta apenas". NÃO há métodos de criação/edição/exclusão manuais.
 * 
 * Rastreabilidade:
 *    - Cada mudança de status registra: data/hora, usuário responsável e status.
 *    - Permite auditoria completa do ciclo de vida do pedido.
 * 
 * Este Service NÃO sabe nada sobre HTTP, Apenas processa e lança exceções de
 * negócio.
 */
@Service
public class StatusPedidoHistoricoService {
	
	private final PedidoRepository pedidoRepository;
	private final StatusPedidoHistoricoRepository historicoRepository;

	public StatusPedidoHistoricoService(StatusPedidoHistoricoRepository historicoRepository, PedidoRepository pedidoRepository) {
		
		this.historicoRepository = historicoRepository;
		this.pedidoRepository = pedidoRepository;
	}
	
	/**
	 *  Retorna o histórico completo de um pedido, ordenado do mais antigo para o mais recente
	 * 
	 * @param pedidoId ID do pedido
     * @return lista de registros de histórico
     * @throws PedidoNaoEncontradoException se o pedido não existir
	 */
	@Transactional(readOnly = true)
	public List<StatusPedidoHistorico> historicoDoPedido(Integer pedidoId){
		Pedido pedido = pedidoRepository.findById(pedidoId)
				.orElseThrow(() -> new PedidoNaoEncontradoException(pedidoId));
		
		// O proprio pedido jpa possui uma lista de histórico carregada (LAZY por padrão)
		// Como estamos em uma transação, podemos acessá-la.
		List<StatusPedidoHistorico> historico = pedido.getHistoricoStatus();
		
		// Ordena por data/hora (mais antifo primeiro)
		historico.sort((h1, h2) -> h1.getDataHora().compareTo(h2.getDataHora()));

		return historico;
	}
	
	/**
	 * Retorna o último status registrado para um pedido.
     * Útil para consultas rápidas.
     * 
     * @param pedidoId ID do pedido
     * @return o último registro de histórico ou null se não houver
	 */
	@Transactional(readOnly = true)
	public StatusPedidoHistorico ultimoStatus(Integer pedidoId){
		
		List<StatusPedidoHistorico> historico = historicoDoPedido(pedidoId);
		
		if(historico.isEmpty()) {
			return null;
		}
		
		return historico.get(historico.size() - 1);
	}
	
	/**
    * Calcula o tempo total que o pedido permaneceu em um determinado status.
    * 
    * Exemplo: Quanto tempo o pedido ficou "EM_PREPARACAO"?
    * 
    * @param pedidoId ID do pedido
    * @param statusAlvo status que se deseja medir
    * @return duração em minutos, ou 0 se não passou pelo status
    */
   @Transactional(readOnly = true)
   public Long tempoNoStatus(Integer pedidoId, StatusPedido statusAlvo) {
	   
	   List<StatusPedidoHistorico> historico = historicoDoPedido(pedidoId);
	   
	   if(historico.size() < 2 ) return 0L;
	   
	   LocalDateTime inicio = null;
	   LocalDateTime fim = null;
	   
	   for(int i = 0; i < historico.size(); i++) {
		   StatusPedidoHistorico atual = historico.get(i);
		   if(atual.getStatus() == statusAlvo && inicio == null) {
			   inicio = atual.getDataHora();
		   }
		   
		   // Procura a próxima mudança após entrar no status alvo
		   if(inicio != null && atual.getStatus() == statusAlvo) {
			   fim = atual.getDataHora();
			   break;
		   }
	   }
	   
	   if (inicio != null && fim != null) {
           return java.time.Duration.between(inicio, fim).toMinutes();
       }
	   
	   return 0L;
   }
	
	/**
     * Lista todos os pedidos que passaram por um determinado status em um período.
     * Útil para relatórios operacionais.
     */
    @Transactional(readOnly = true)
    public List<StatusPedidoHistorico> buscarPorStatusEPeriodo(StatusPedido status,
                                                               LocalDateTime inicio,
                                                               LocalDateTime fim) {
        return historicoRepository.findByStatusAndDataHoraBetweenOrderByDataHoraDesc(status, inicio, fim);
    }

}
