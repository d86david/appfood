package com.dsys.appfood.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dsys.appfood.domain.model.Endereco;

@Repository
public interface EnderecoRepository extends JpaRepository<Endereco, Integer> {

	//--- PESQUISA POR ENDERECO ---
	List<Endereco> findByEnderecoContainingIgnoreCase(String endereco);

	//--- PESQUISA ENDERECO POR CEP ---
	List<Endereco> findByCep(String cep);

	//--- PESQUISA ENDERECO POR BAIRRO---
	List<Endereco> findByBairro(String bairro);

}
