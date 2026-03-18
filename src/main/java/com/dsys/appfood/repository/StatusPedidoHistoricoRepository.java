package com.dsys.appfood.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dsys.appfood.domain.model.StatusPedidoHistorico;

@Repository
public interface StatusPedidoHistoricoRepository extends JpaRepository<StatusPedidoHistorico, Integer>{
	

}
