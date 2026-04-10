package com.dsys.appfood.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dsys.appfood.domain.model.Borda;

@Repository
public interface BordaRepository extends JpaRepository<Borda, Integer> {

	// BUSCA BORDA PELO NOME
	Optional<Borda> findByNomeIgnoreCase(String nome);

	// LISTA BORDA PELO NOME
	List<Borda> findByNomeContainingIgnoreCase(String nome);

}
