package com.dsys.appfood.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dsys.appfood.domain.model.Impressora;

public interface ImpressoraRepository extends JpaRepository<Impressora, Integer>{
	
	
	// BUSCA A IMRESSORA PELO NOME (case insensitive).
	Optional<Impressora> findByNomeIgnoreCase(String nome);
	
	// LISTA APENAS IMPRESSORAS ATIVAS
	List<Impressora> findByAtivaTrue(); // Usado na tela de configuração para evitar mostrar impressoras quebradas.

}
