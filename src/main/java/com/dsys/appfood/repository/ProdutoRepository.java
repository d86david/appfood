package com.dsys.appfood.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dsys.appfood.domain.model.Categoria;
import com.dsys.appfood.domain.model.Produto;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Integer>{

	// --- LISTAR PRODUTOS PELO NOME ---
	List<Produto> findByNomeContainingIgnoreCase(String nome);
	
	// --- LISTAR PRODUTOS PELA CATEGORIA (Objeto)---
	List<Produto> findByCategoria(Categoria categoria);
	
	// --- LISTAR PRODUTOS PELO ID DA CATEGORIA --- 
	//Util quando só tem o ID - evita buscar a Categoria só para depois buscar os produtos
	List<Produto> findByCategoriaId(Integer categoriaId);
	
	// --- VERIFICAR SE EXISTE PRODUTO COM ESSE NOME NA MESMA CATEGORIA ---
    // Vai ser necessário no Service para evitar duplicata
    // Ex: não pode ter duas "Pizza Calabresa" na categoria Pizzas
	boolean existsByNomeIgnoreCaseAndCategoria(String nome, Categoria categoria);
	
	
}
