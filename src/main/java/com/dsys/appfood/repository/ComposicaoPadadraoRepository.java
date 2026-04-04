package com.dsys.appfood.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dsys.appfood.domain.model.ComposicaoPadrao;

@Repository
public interface ComposicaoPadadraoRepository extends JpaRepository<ComposicaoPadrao, Integer> {

	// --- VERIFICAR SE EXISTE INGREDIENTE COM ESSA COMPOSIÇÃO
	boolean existsByIngredientesId(Integer id);
	
	// --- BUSCA PRODUTO NA COMPOSIÇÃO PADRÃO
	Optional<ComposicaoPadrao> findByProdutoId(Integer produtoId);

	// Traz todas as composições (receitas) que levam este ingrediente
	List<ComposicaoPadrao> findByIngredientesId(Integer ingredienteId);

}
