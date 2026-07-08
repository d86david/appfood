package com.dsys.appfood.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.dsys.appfood.domain.model.Mesa;

public interface MesaRepository extends JpaRepository<Mesa, Integer>{

	//BUSCA MESA PELO NUMERO
	Optional<Mesa> findByNumero(Integer numero);

	// LISTAR TODAS MESAS ORDENANDO POR NUMERO
	Page<Mesa> findAllByOrderByNumeroAsc(Pageable pageable);

	//LISTAR MESAS LIVRES ORDENANDO POR NUMERO
	Page<Mesa> findByOcupadaFalseAndAtivaTrueOrderByNumeroAsc(Pageable pageable);

	// LISTAR MESAS OCUPADAS E ATIVAS ORDENANDO POR NUMERO
	Page<Mesa> findByOcupadaTrueAndAtivaTrueOrderByNumeroAsc(Pageable pageable);

	//VERIFICA SE MESA EXISTE
	boolean existsByNumero(Integer numero);

}
