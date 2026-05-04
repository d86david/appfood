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
import org.springframework.web.bind.annotation.RequestParam;
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
	public ResponseEntity<CategoriaResponse> cadastrar(@RequestBody @Valid CategoriaRequest request,
			UriComponentsBuilder uriBuilder) {

		// 1. Chamar o Service para executar as regras de negócio e salvar
		CategoriaResponse response = categoriaService.cadastrarCategoriaResponse(request);

		// 2. Retornar o código 201 (Created) e a URL do novo recurso
		URI uri = uriBuilder.path("/categorias/{id}").buildAndExpand(response.id()).toUri();

		// 3. Devolver o DTO de Saída (Response)
		return ResponseEntity.created(uri).body(response);

	}

	// ====================================================================================================
	// 2. EDITAR CATEGORIA
	// ====================================================================================================
	@PutMapping("/{id}")
	public ResponseEntity<CategoriaResponse> atualizar(@PathVariable Integer id,
			@RequestBody @Valid CategoriaRequest request) {

		// 1. Chamar o Service para executar as regras de negócio e atualizar
		CategoriaResponse response = categoriaService.editarCategoriaResponse(id, request);

		// 2. Devolver o DTO de Saída (Response)
		return ResponseEntity.ok(response);

	}

	// ====================================================================================================
	// 3. EXCLUIR CATEGORIA
	// ====================================================================================================
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> excluir(@PathVariable Integer id) {
		categoriaService.excluirCategoria(id);
		return ResponseEntity.noContent().build();
	}

	// ====================================================================================================
	// 4. BUSCAR CATEGORIA POR ID
	// ====================================================================================================
	@GetMapping("/{id}")
	public ResponseEntity<CategoriaResponse> buscarPorId(@PathVariable Integer id) {

		Categoria categoria = categoriaService.buscarCategoriaPorId(id);

		return ResponseEntity.ok(CategoriaResponse.from(categoria));
	}

	// ====================================================================================================
	// 5. BUSCAS CATEGORIA POR NOME E LISTAS
	// ====================================================================================================
	@GetMapping
	public ResponseEntity<List<CategoriaResponse>> listar(@RequestParam(required = false) String nome) {

		List<CategoriaResponse> lista;

		if (nome != null) {
			lista = categoriaService.buscarPorNomeResponse(nome);
		} else {
			lista = categoriaService.listarTodasCategoriasResponse();
		}

		return ResponseEntity.ok(lista);
	}

}
