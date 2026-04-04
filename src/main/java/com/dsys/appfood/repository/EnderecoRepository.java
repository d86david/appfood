package com.dsys.appfood.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dsys.appfood.domain.model.Endereco;

@Repository
public interface EnderecoRepository extends JpaRepository<Endereco, Integer> {

	//--- PESQUISA POR LOGRADOURO ---
	List<Endereco> findByLogradouroContainingIgnoreCase(String logradouro);

	//--- PESQUISA ENDERECO POR CEP ---
	Optional<Endereco> findByCep(String cep);

	//--- PESQUISA ENDERECO POR BAIRRO---
	List<Endereco> findByBairroContainingIgnoreCase(String bairro);

}
