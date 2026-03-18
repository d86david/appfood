package com.dsys.appfood.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dsys.appfood.domain.enums.StatusPedido;
import com.dsys.appfood.domain.model.Pedido;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Integer>{

	/**
	 * Metodo de pesquisa por Id de cliente
	 * O Spring gera o SQL "SELECT * FROM pedido WHERE cliente_id = ?"
	 * @param clienteId
	 * @return
	 */
	List<Pedido> findByClienteId(Integer clienteId);
	
	//--- PESQUISAR POR STATUS DE PEDIDO ---
	List<Pedido> findByStatus(StatusPedido status);
}
