package com.dsys.appfood.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dsys.appfood.domain.model.Pagamento;

@Repository
public interface PagamentoRepository extends JpaRepository<Pagamento, Integer>{
	
	// Busca pagamentos de um pedido, ordenados do mais recente para o mais antigo
    List<Pagamento> findByPedidoIdOrderByDataHoraDesc(Integer pedidoId);
	

}
