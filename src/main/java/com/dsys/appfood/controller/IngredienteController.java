package com.dsys.appfood.controller;


import com.dsys.appfood.dto.request.IngredienteRequest;
import com.dsys.appfood.dto.response.IngredienteResponse;
import com.dsys.appfood.service.IngredienteService;

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
@RequestMapping("/api/ingredientes")
public class IngredienteController {
	
	
	private final IngredienteService ingredienteService;

	public IngredienteController(IngredienteService ingredienteService) {
		
		this.ingredienteService = ingredienteService;
		
	}
	
	// ====================================================================================================
	// 1. CADASTRAR NOVO INGREDIENTE
	// ====================================================================================================
	
	@PostMapping
	public ResponseEntity<IngredienteResponse> cadastrar(@RequestBody @Valid IngredienteRequest request,
			UriComponentsBuilder uriBuilder){
		
		// 1. Chamar o Service para executar as regras de negócio e salvar
		IngredienteResponse response = ingredienteService.cadastrarIngredienteResponse(request);
		
		// 2. Retornar o código 201 (Created) e a URL do novo recurso
		URI uri = uriBuilder.path("ingredientes/{id}").buildAndExpand(response.id()).toUri();
	
		// 3. Devolver o DTO de Saída (Response)
		return ResponseEntity.created(uri).body(response);
		
	}
	
	// ====================================================================================================
	// 2. EDITAR INGREDIENTE
	// ====================================================================================================
	@PutMapping("/{id}")
	public ResponseEntity<IngredienteResponse> atualizar(@PathVariable Integer id, 
						@RequestBody @Valid IngredienteRequest request){
		
		IngredienteResponse response = ingredienteService.editarIngredienteResponse(id, request);
		
		return ResponseEntity.ok(response);
		
	}
	
	// ====================================================================================================
	// 3. EXCLUIR INGREDIENTE
	// ====================================================================================================
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> excluir (@PathVariable Integer id){
		
		ingredienteService.excluirIngrediente(id);
		
		return ResponseEntity.noContent().build();
		
	}
	
	// ====================================================================================================
	// 4. BUSCAR INGREDIENTE POR ID
	// ====================================================================================================
	@GetMapping("/{id}")
	public ResponseEntity<IngredienteResponse> buscarPorId(@PathVariable Integer id){
		
		IngredienteResponse response = ingredienteService.buscarPorIdResponse(id);
		
		return ResponseEntity.ok(response);
		
	}
	
	
	// ====================================================================================================
	// 5. BUSCAR INGREDIENTE POR NOME E LISTA 
	// ====================================================================================================
	@GetMapping
	public ResponseEntity<List<IngredienteResponse>> buscarPorNome(@RequestParam(required = false) String nome){
		
		List<IngredienteResponse> lista;
		
		if(nome != null) {
			lista = ingredienteService.buscarPorNomeResponse(nome);
		}else {
			lista = ingredienteService.listarTodosIngredientesResponse();
		}
		
		return ResponseEntity.ok(lista);
	}
}
