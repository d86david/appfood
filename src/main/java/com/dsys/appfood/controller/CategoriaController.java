package com.dsys.appfood.controller;

import com.dsys.appfood.domain.model.Categoria;
import com.dsys.appfood.dto.CategoriaRequest;
import com.dsys.appfood.dto.CategoriaResponse;
import com.dsys.appfood.service.CategoriaService;

import jakarta.validation.Valid;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {
	
	
	private final CategoriaService categoriaService;

	public CategoriaController(CategoriaService categoriaService) {
		
		this.categoriaService = categoriaService;
		
	}
	
	// ====================================================================================================
    // 1. CADASTRAR NOVA CATEGORIA
    // ====================================================================================================
	@PostMapping
	public ResponseEntity<CategoriaResponse> cadastrar(
			@RequestBody @Valid CategoriaRequest request,
			UriComponentsBuilder uriBuilder){
		
		// 1. Chamar o Service para executar as regras de negócio e salvar 
		Categoria categoriaSalva = categoriaService.cadastrarCategoria(request.nome(), request.personalizavel());
		
		// 2. Retornar o código 201 (Created) e a URL do novo recurso
		URI uri = uriBuilder.path("/categorias/{id}").buildAndExpand(categoriaSalva.getId()).toUri();
		
		// 3. Devolver o DTO de Saída (Response)
		return ResponseEntity.created(uri).body(CategoriaResponse.from(categoriaSalva));
		
	}
	
	// ====================================================================================================
    // 2. EDITAR CATEGORIA
    // ====================================================================================================
	@PutMapping("/{id}")
	public ResponseEntity<CategoriaResponse> atualizar(
			@PathVariable Integer id, @RequestBody @Valid CategoriaRequest request){
		
		// 1. Chamar o Service para executar as regras de negócio e atualizar 
		Categoria categoriaAtualizada = categoriaService.editarCategoria(id, request.nome(), request.personalizavel());
		
		// 2. Devolver o DTO de Saída (Response)
		return ResponseEntity.ok(CategoriaResponse.from(categoriaAtualizada));
		
	}
	
	// ====================================================================================================
    // 3. EXCLUIR CATEGORIA
    // ====================================================================================================
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> excluir(@PathVariable Integer id){
		categoriaService.excluirCategoria(id);
		return ResponseEntity.noContent().build();
	}
	
	// ====================================================================================================
    // 4. BUSCAR CATEGORIA POR ID
    // ====================================================================================================
	@GetMapping("/{id}")
	public ResponseEntity<CategoriaResponse> buscarPorId(@PathVariable Integer id){
		
		Categoria categoria = categoriaService.buscarCategoriaPorId(id);
		
		return ResponseEntity.ok(CategoriaResponse.from(categoria));
	}
	
	// ====================================================================================================
    // 5. BUSCAS CATEGORIA POR NOME
    // ====================================================================================================
	@GetMapping("/{nome}")
	public ResponseEntity<CategoriaResponse> buscarPorNome(@PathVariable String nome){
		
		Categoria categoria =  categoriaService.buscarCategoriaPorNome(nome);
		
		return ResponseEntity.ok(CategoriaResponse.from(categoria));
		
	}
	
	
	// ====================================================================================================
    // 6. LISTAR TODAS AS CATEGORIAS
    // ====================================================================================================
	@GetMapping
	public ResponseEntity<List<CategoriaResponse>> listar(){
		List<CategoriaResponse> lista = categoriaService.listarTodasCategorias()
				.stream()
				.map(CategoriaResponse::from)
				.toList();
		
		return ResponseEntity.ok(lista);
	}
	
}
