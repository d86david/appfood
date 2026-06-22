package com.dsys.appfood.repository;


import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dsys.appfood.domain.model.Categoria;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {

	// --- LISTAR CATERORIA POR NOME ---
	Optional<Categoria> findByNomeIgnoreCase(String nome);

	// --- LISTAR CATERORIA POR NOME ---
	Page<Categoria> findByNomeContainingIgnoreCase(String nome, Pageable pageable);

}
