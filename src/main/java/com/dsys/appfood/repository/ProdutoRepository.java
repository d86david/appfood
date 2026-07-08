package com.dsys.appfood.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dsys.appfood.domain.model.Categoria;
import com.dsys.appfood.domain.model.Produto;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Integer>{

	// --- BUSCAR PRODUTO PELO NOME E CATEGORIA ---
	Optional<Produto> findByNomeIgnoreCaseAndCategoria(String nome, Categoria categoria);

	// --- LISTAR PRODUTOS PELO NOME ---
	Page<Produto> findByNomeContainingIgnoreCase(String nome, Pageable pageable);

	// --- LISTAR PRODUTOS PELA CATEGORIA (Objeto)---
	Page<Produto> findByCategoria(Categoria categoria, Pageable pageable);

	// --- LISTAR PRODUTOS PELO ID DA CATEGORIA ---
	//Util quando só tem o ID - evita buscar a Categoria só para depois buscar os produtos
	Page<Produto> findByCategoriaId(Integer categoriaId, Pageable pageable);

	// --- VERIFICAR SE EXISTE PRODUTO COM ESSE NOME NA MESMA CATEGORIA ---
    // Vai ser necessário no Service para evitar duplicata
    // Ex: não pode ter duas "Pizza Calabresa" na categoria Pizzas
	boolean existsByNomeIgnoreCaseAndCategoria(String nome, Categoria categoria);

	// --- VERIFICAR SE EXISTE PRODUTO COM NA CATEGORIA PELO ID ---
	boolean existsByCategoriaId(Integer categoriaId);


}
