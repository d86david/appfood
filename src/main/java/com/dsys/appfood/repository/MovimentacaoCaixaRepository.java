package com.dsys.appfood.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dsys.appfood.domain.model.MovimentacaoCaixa;
import java.time.LocalDateTime;


@Repository
public interface MovimentacaoCaixaRepository extends JpaRepository<MovimentacaoCaixa, Integer>{
	
	
	///--- PESQUISAR MOVIMENTAÇÃO POR DATA  ---
	List<MovimentacaoCaixa> findByDataHoraMovimentoBetween(LocalDateTime dataInicio, LocalDateTime dataFim);

}
