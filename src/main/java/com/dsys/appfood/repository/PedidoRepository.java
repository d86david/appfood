package com.dsys.appfood.repository;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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
	Page<Pedido> findByStatusAndTipo(StatusPedido status, TipoPedido tipo, Pageable pageable);

	//--- PESQUISAR PEDIDOS EM ABERTO
	/**
     * Busca pedidos que NÃO estão FINALIZADOS nem CANCELADOS.
     * Ou seja: PEDIDO_INICIADO, PENDENTE, EM_PREPARACAO, PRONTO, SAIU_PARA_ENTREGA
     *
     * @param statusFechados Lista de status que encerram o pedido (FINALIZADO, CANCELADO)
     * @return Página de pedidos em aberto, ordenados por data de abertura (mais recente primeiro)
     */
	Page<Pedido> findByStatusNotIn(@Param("statusFechados") List<StatusPedido> statusFechado, Pageable pageable);

	// =============================================================
    // MÉTODO BUSCA COMPLETA (JOIN FETCH + @EntityGraph)
    // =============================================================

    /**
     * Busca um pedido completo para operações que precisam de tudo:
     * - Itens, subitens, customizações, bordas, produtos, categorias
     * - Pagamentos, cliente, endereço, entregador, operador
     *
     * ESTRATÉGIA:
     * - Use @EntityGraph para carregar as coleções principais (itens, subitens, etc.)
     * - Use JOIN FETCH para carregar relacionamentos "singulares" (cliente, entregador)
     *
     * Por que esta abordagem?
     * - @EntityGraph lida bem com coleções (List), evitando duplicatas.
     * - JOIN FETCH é ótimo para relações 1-1 ou ManyToOne (cliente, entregador).
     *
     * @param id ID do pedido
     * @return Pedido completo
     */
	@EntityGraph(value = "Pedido.completo", type = EntityGraph.EntityGraphType.LOAD)
	@Query("SELECT DISTINCT p FROM Pedido p " +
	           "LEFT JOIN FETCH p.cliente c " +
	           "LEFT JOIN FETCH c.endereco " +
	           "LEFT JOIN FETCH p.operador op " +
	           "LEFT JOIN FETCH p.entregador ent " +
	           "LEFT JOIN FETCH p.pagamentos pag " +
	           "WHERE p.id = :id")
	    Optional<Pedido> findByIdCompleto(@Param("id") Integer id);

	// =============================================================
    // MÉTODO BUSCA PARA IMPRESSÃO (APENAS O NECESSÁRIO)
    // =============================================================
	/**
     * Busca um pedido com todos os dados necessários para impressão.
     *
     * DIFERENÇA do findByIdCompleto:
     * - Carrega TUDO que a impressão precisa (itens, subitens, customizações).
     * - Usa apenas @EntityGraph, sem JOIN FETCH adicionais.
     *
     * Esta abordagem é mais limpa e evita SQL complexos.
     *
     * @param id ID do pedido
     * @return Pedido com dados para impressão
     */
	@EntityGraph(value = "Pedido.completo", type = EntityGraph.EntityGraphType.LOAD)
	@Query("SELECT p FROM Pedido p WHERE p.id = :id")
    Optional<Pedido> findByIdCompletoForPrinting(@Param("id") Integer id);

 // =============================================================
    // MÉTODO BUSCA RESUMIDA (PARA LISTAGENS)
    // =============================================================

    /**
     * Busca um pedido com dados resumidos (sem itens).
     *
     * USO: Listagens de pedidos, relatórios rápidos.
     *
     * @param id ID do pedido
     * @return Pedido com dados resumidos
     */
    @EntityGraph(value = "Pedido.resumo", type = EntityGraph.EntityGraphType.LOAD)
    @Query("SELECT p FROM Pedido p WHERE p.id = :id")
    Optional<Pedido> findByIdResumido(@Param("id") Integer id);

    @EntityGraph(value = "Pedido.resumo", type = EntityGraph.EntityGraphType.LOAD)
    Page<Pedido> findAllResumido(Pageable pageable);

}
