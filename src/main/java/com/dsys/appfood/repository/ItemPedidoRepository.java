package com.dsys.appfood.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dsys.appfood.domain.model.ItemPedido;

@Repository
public interface ItemPedidoRepository extends JpaRepository<ItemPedido, Integer>{
	
	
	List<ItemPedido> findByPedidoDtHoraAberturaBetween(LocalDateTime inicio, LocalDateTime fim);
	
}
