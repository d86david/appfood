package com.dsys.appfood.repository;


import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dsys.appfood.domain.model.Borda;

@Repository
public interface BordaRepository extends JpaRepository<Borda, Integer> {

	// BUSCA BORDA PELO NOME
	Optional<Borda> findByNomeIgnoreCase(String nome);
	
	// LISTA BORDA PELO NOME
	Page<Borda> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
}
