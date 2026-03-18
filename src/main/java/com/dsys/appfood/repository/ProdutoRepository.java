package com.dsys.appfood.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dsys.appfood.domain.model.Produto;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Integer>{

	List<Produto> findByNomeContainingIgnoreCase(String nome);
	
}
