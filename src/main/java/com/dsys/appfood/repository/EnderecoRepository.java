package com.dsys.appfood.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dsys.appfood.domain.model.Endereco;

@Repository
public interface EnderecoRepository extends JpaRepository<Endereco, Integer> {

	//--- PESQUISA POR LOGRADOURO ---
	Page<Endereco> findByLogradouroContainingIgnoreCase(String logradouro, Pageable pageable);

	//--- PESQUISA ENDERECO POR CEP ---
	Page<Endereco> findByCepContainingIgnoreCase(String cep, Pageable pageable);

	//--- PESQUISA ENDERECO POR BAIRRO---
	Page<Endereco> findByBairroContainingIgnoreCase(String bairro, Pageable pageable);

}
