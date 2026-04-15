package com.dsys.appfood.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dsys.appfood.domain.enums.TipoConta;
import com.dsys.appfood.domain.model.ContaCorrente;

@Repository
public interface ContaCorrenteRepository extends JpaRepository<ContaCorrente, Integer>{
	
	//--- BUSCAR CONTA POR NOME ---
	Optional<ContaCorrente> findByNomeIgnoreCase(String nome);
    
	//--- BUSCAR POR CONTA ATIVA  ---
	List<ContaCorrente> findByAtivaTrue();
    
	//--- BUSCAR CONTA POR TIPO ---
    List<ContaCorrente> findByTipo(TipoConta tipo);

}
