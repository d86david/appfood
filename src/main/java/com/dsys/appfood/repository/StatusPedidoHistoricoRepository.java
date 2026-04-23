package com.dsys.appfood.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dsys.appfood.domain.enums.StatusPedido;
import com.dsys.appfood.domain.model.StatusPedidoHistorico;

@Repository
public interface StatusPedidoHistoricoRepository extends JpaRepository<StatusPedidoHistorico, Integer>{
	
	
	List<StatusPedidoHistorico> findByStatusAndDataHoraBetweenOrderByDataHoraDesc(StatusPedido status,LocalDateTime inicio,LocalDateTime fim);
	

}
