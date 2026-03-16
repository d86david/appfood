package com.dsys.appfood.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dsys.appfood.domain.model.Cliente;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer>{
	
	//--- BUSCAR CLIENTE POR NOME ---
	List<Cliente> findByNome(String nome);
	
	//--- BUSCAR CLIENTE POR TELEFONE ---
	Optional<Cliente> findByFirstOrderByTelefone(String telefone);

}
