package com.dsys.appfood.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dsys.appfood.domain.model.Tamanho;

@Repository
public interface TamanhoRepository extends JpaRepository<Tamanho, Integer>{
	
	//--- BUSCAR TAMANHO POR NOME ---
	Optional<Tamanho> findByNomeIgnoreCase(String nome);
	
	//--- LISTAR TAMANHO POR NOME ---
	List<Tamanho> findByNomeContainingIgnoreCase(String nome);

}
