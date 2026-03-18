package com.dsys.appfood.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dsys.appfood.domain.model.Ingrediente;

@Repository
public interface IngredienteRepository extends JpaRepository<Ingrediente, Integer>{

}
