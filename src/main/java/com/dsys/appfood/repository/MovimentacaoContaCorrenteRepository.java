package com.dsys.appfood.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dsys.appfood.domain.model.MovimentacaoContaCorrente;

@Repository
public interface MovimentacaoContaCorrenteRepository extends JpaRepository<MovimentacaoContaCorrente, Integer>{
	
	
	List<MovimentacaoContaCorrente> findByContaIdOrderByDataHoraDesc(Integer contaId);
	
	
    List<MovimentacaoContaCorrente> findByContaIdAndDataHoraBetween(Integer contaId, LocalDateTime inicio, LocalDateTime fim);

}
