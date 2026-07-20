package com.dsys.appfood.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dsys.appfood.domain.model.CanalImpressao;

public interface CanalImpressaoRepository extends JpaRepository<CanalImpressao, Integer>{

	// BUSCA A CANAL PELO NOME (case insensitive).
	Optional<CanalImpressao> findByNomeIgnoreCase(String nome);
	
	// LISTA APENAS CANAIS ATIVOS
	 List<CanalImpressao> findByAtivoTrue();
	
}
