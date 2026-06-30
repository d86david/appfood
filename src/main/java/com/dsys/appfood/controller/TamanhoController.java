package com.dsys.appfood.controller;

import com.dsys.appfood.service.TamanhoService;

import java.net.URI;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

import com.dsys.appfood.dto.request.TamanhoRequest;
import com.dsys.appfood.dto.response.TamanhoResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tamanhos")
public class TamanhoController {
	
	private final TamanhoService tamanhoService;

	public TamanhoController(TamanhoService tamanhoService) {
		this.tamanhoService = tamanhoService;
	}
	
	// ====================================================================================================
	// 1. CADASTRAR NOVO TAMANHO
	// ====================================================================================================
	
	@PostMapping
	public ResponseEntity<TamanhoResponse> cadastrar(@RequestBody @Valid TamanhoRequest request,
							UriComponentsBuilder uriBulider){
		
		// 1. Chamar o Service para executar as regras de negócio e salvar
		TamanhoResponse response = tamanhoService.cadastrarTamanhoResponse(request);
		
		// 2. Retornar o código 201 (Created) e a URL do novo recurso
		URI uri = uriBulider.path("/api/tamanhos/{id}").buildAndExpand(response.id()).toUri();
		
		// 3. Devolver o DTO de Saída (Response)
		return ResponseEntity.created(uri).body(response);
		
	}
	
	
	// ====================================================================================================
	// 2. EDITAR TAMANHO
	// ====================================================================================================
	@PutMapping("/{id}")
	public ResponseEntity<TamanhoResponse> atualizar(@PathVariable Integer id, @RequestBody @Valid TamanhoRequest request){
		
		TamanhoResponse response = tamanhoService.editarTamanhoResponse(id, request);
		
		return ResponseEntity.ok(response);
	}
	
	
	// ====================================================================================================
	// 3. EXCLUIR TAMANHO
	// ====================================================================================================
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> excluir(@PathVariable Integer id){
		
		tamanhoService.excluirTamanho(id);
		
		return ResponseEntity.noContent().build();
		
	}
	
	
	// ====================================================================================================
	// 4. BUSCAR TAMANHO POR ID
	// ====================================================================================================
	@GetMapping("/{id}")
	public ResponseEntity<TamanhoResponse> buscarPorId(@PathVariable Integer id){
		
		TamanhoResponse response = tamanhoService.buscarPorIdResponse(id);
		
		return ResponseEntity.ok(response);
		
	}
	
	// ====================================================================================================
	// 5. BUSCAR POR NOME E LISTAR 
	// ====================================================================================================
	@GetMapping
	public ResponseEntity<Page<TamanhoResponse>> listar(@RequestParam (required = false) String nome, Pageable pageable){
		
		Page<TamanhoResponse> paginaResultados;
		
		if(nome != null ) {
			paginaResultados = tamanhoService.buscarPorNomeResponse(nome, pageable);
		}else {
			paginaResultados = tamanhoService.listarTodasOsTamanhosResponse(pageable);
		}
		
		return ResponseEntity.ok(paginaResultados);
	}
}
