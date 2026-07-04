package com.dsys.appfood.repository;


import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dsys.appfood.domain.enums.StatusPedido;
import com.dsys.appfood.domain.enums.TipoPedido;
import com.dsys.appfood.domain.model.Pedido;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Integer>{

	/**
	 * Metodo de pesquisa por Id de cliente
	 * O Spring gera o SQL "SELECT * FROM pedido WHERE cliente_id = ?"
	 */
	List<Pedido> findByClienteId(Integer clienteId);
		
	// --- PESQUISAR PEDIDOS POR TIPO NUMERO DA MESA E STATUS
	List<Pedido> findByTipoAndNumeroMesaAndStatusNotIn(TipoPedido tipo, Integer numeroMesa, List<StatusPedido> status);
	
	// --- LISTA PEDIDO POR PERÍODO ---
	List<Pedido> findByDtHoraAberturaBetween(LocalDateTime inicio, LocalDateTime fim);
	
	// --- LISTA PEDIDO POR TIPO, INICIO E FECHAMENTO 
	List<Pedido> findByTipoAndDtHoraAberturaBetween( TipoPedido tipo, LocalDateTime inicio, LocalDateTime fim);
	
	//--- PESQUISAR POR STATUS DE PEDIDO ORDENADO POR HORA DA ABERTURA ---
	List<Pedido> findByStatusInOrderByDtHoraAberturaAsc(List<StatusPedido> statusPendentes);
	
	//--- PESQUISAR PEDIDOS POR STATUS COM PAGINAÇÃO ---
	Page<Pedido> findByStatus(StatusPedido status, Pageable pageable );
	
	//--- PESQUISAR PEDIDOS POR TIPO COM PAGINAÇÃO ---
	Page<Pedido> findByTipo (TipoPedido tipo, Pageable pageable);
	
	//--- PESQUISAR PEDIDOS POR SATUS E TIPO COM PAGINAÇÃO
	Page<Pedido> finByStatusAndTipo(StatusPedido status, TipoPedido tipo, Pageable pageable);
	
	//--- PESQUISAR PEDIDOS EM ABERTO
	/**
     * Busca pedidos que NÃO estão FINALIZADOS nem CANCELADOS.
     * Ou seja: PEDIDO_INICIADO, PENDENTE, EM_PREPARACAO, PRONTO, SAIU_PARA_ENTREGA
     * 
     * @param statusFechados Lista de status que encerram o pedido (FINALIZADO, CANCELADO)
     * @return Página de pedidos em aberto, ordenados por data de abertura (mais recente primeiro)
     */
	Page<Pedido> findByStatusNotIn(@Param("statusFechados") List<StatusPedido> statusFechado, Pageable pageable);
	
}
