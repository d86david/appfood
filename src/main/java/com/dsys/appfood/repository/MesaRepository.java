package com.dsys.appfood.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dsys.appfood.domain.model.Mesa;

public interface MesaRepository extends JpaRepository<Mesa, Integer>{
	
	//BUSCA MESA PELO NUMERO
	Optional<Mesa> findByNumero(Integer numero);
	
	// LISTAR TODAS MESAS ORDENANDO POR NUMERO
	List<Mesa> findAllByOrderByNumeroAsc();
	
	//LISTAR MESAS LIVRES ORDENANDO POR NUMERO
	List<Mesa> findByOcupadaFalseAndAtivaTrueOrderByNumeroAsc();
	
	//VERIFICA SE MESA EXISTE
	boolean existsByNumero(Integer numero);

}
