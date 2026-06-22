package com.dsys.appfood.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dsys.appfood.domain.model.Entregador;

@Repository
public interface EntregadorRepository extends JpaRepository<Entregador, Integer>{

	//PESQUISA ENTREGADOR PELO NOME
	Page<Entregador> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
	
	//PESQUISA SE EXISTE ENTREAGOS ATIVO COM O TELEFONE
	boolean existsByTelefoneAndAtivoTrue(String telefone);
	
	//PESQUISA ENTREGADOR PELO TELEFONE
	Optional<Entregador> findByTelefoneIgnoreCaseAndAtivoTrue(String telefone);
	
	//VERIFICA SE ENTREGADOR ESTÁ ATIVO PELO ID
	boolean existsByIdAndAtivoTrue(Integer id);
	
}
