package com.dsys.appfood.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dsys.appfood.domain.model.Cliente;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer>{

	//--- LISTAR CLIENTE POR NOME ---
	Page<Cliente> findByNomeContainingIgnoreCase(String nome, Pageable pageable);

	//--- BUSCAR CLIENTE POR TELEFONE PRINCIPAL---
	Optional<Cliente> findByTelefonePrincipal(String telefone);

	// --- VERIFICA SE CLIENTE EXISTE E ESTA ATIVO PELO TELEFONE ---
	boolean existsByTelefonePrincipalAndAtivoTrue(String telefone);

}
