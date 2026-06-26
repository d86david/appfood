package com.dsys.appfood.repository;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dsys.appfood.domain.enums.StatusPedido;
import com.dsys.appfood.domain.model.StatusPedidoHistorico;

@Repository
public interface StatusPedidoHistoricoRepository extends JpaRepository<StatusPedidoHistorico, Integer>{
	
	
	Page<StatusPedidoHistorico> findByStatusAndDataHoraBetweenOrderByDataHoraDesc(StatusPedido status,LocalDateTime inicio,LocalDateTime fim, Pageable pageable);
	

}
