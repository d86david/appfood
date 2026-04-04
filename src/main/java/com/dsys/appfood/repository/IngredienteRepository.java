package com.dsys.appfood.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dsys.appfood.domain.model.Ingrediente;

@Repository
public interface IngredienteRepository extends JpaRepository<Ingrediente, Integer>{
	
	//BUSCA INGREDIENTE PELO NOME
	Optional<Ingrediente> findByNomeIgnoreCase(String nome);
	

}
