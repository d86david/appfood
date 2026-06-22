package com.dsys.appfood.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dsys.appfood.domain.model.Tamanho;

@Repository
public interface TamanhoRepository extends JpaRepository<Tamanho, Integer> {

	// --- BUSCAR TAMANHO POR NOME ---
	Optional<Tamanho> findByNomeIgnoreCase(String nome);

	// --- LISTAR TAMANHO POR NOME ---
	Page<Tamanho> findByNomeContainingIgnoreCase(String nome, Pageable pageable);

}
