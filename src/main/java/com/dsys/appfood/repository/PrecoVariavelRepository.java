package com.dsys.appfood.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dsys.appfood.domain.model.PrecoVariavel;
import com.dsys.appfood.domain.model.Produto;
import com.dsys.appfood.domain.model.Tamanho;

@Repository
public interface PrecoVariavelRepository extends JpaRepository<PrecoVariavel, Integer>{
	
	// --- VERIFICAR SE EXISTE PRECO VARIAVEL COM O TAMANNHOID ---
	boolean existsByTamanhoId(Integer id);
	
	// --- BUSCA COMBINAÇÃO DE PREÇOS COM PRODUTO E TAMANNHO ---
	Optional<PrecoVariavel> findByProdutoAndTamanho(Produto produto, Tamanho tamanho);

}
