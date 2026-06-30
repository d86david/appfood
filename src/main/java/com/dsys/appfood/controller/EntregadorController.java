package com.dsys.appfood.controller;

import com.dsys.appfood.dto.request.EntregadorRequest;
import com.dsys.appfood.dto.request.EntregadorStatusRequest;
import com.dsys.appfood.dto.response.EntregadorResponse;
import com.dsys.appfood.service.EntregadorService;

import jakarta.validation.Valid;

import java.net.URI;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api/entregadores")
public class EntregadorController {
	
	private final EntregadorService entregadorService;

	public EntregadorController(EntregadorService entregadorService) {
		
		this.entregadorService = entregadorService;
		
	}
	
	
	// ====================================================================================================
	// 1. CADASTRAR NOVO ENTREGADOR
	// ====================================================================================================
	
	@PostMapping
	public ResponseEntity<EntregadorResponse> cadastrar(@RequestBody @Valid EntregadorRequest request, 
					UriComponentsBuilder uriBuilder){
		// 1. Chamar o Service para executar as regras de negócio e salvar
		
		EntregadorResponse response = entregadorService.cadastrarEntregdorResponse(request);
		
		// 2. Retornar o código 201 (Created) e a URL do novo recurso
		URI uri = uriBuilder.path("/api/entregadores/{id}").buildAndExpand(response.id()).toUri();
		
		// 3. Devolver o DTO de Saída (Response)
		return ResponseEntity.created(uri).body(response);
	}
	
	// ====================================================================================================
	// 2. EDITAR ENTREGADOR
	// ====================================================================================================
	@PutMapping("/{id}")
	public ResponseEntity<EntregadorResponse> atualizar(@PathVariable Integer id,@RequestBody @Valid EntregadorRequest request){
		
		EntregadorResponse response = entregadorService.editarEntregadorResponse(id, request);
		
		return ResponseEntity.ok(response);
		
	}
	
	
	// ====================================================================================================
	// 3. ATIVAR/INATIVAR ENTREGADOR 
	// ====================================================================================================
	@PatchMapping("/{id}/status")
	public ResponseEntity<Void> atualizarSuatus (@PathVariable Integer id,
												@RequestBody @Valid EntregadorStatusRequest request){
		
		entregadorService.alterarStatusEntregadorResponse(id, request);
		
		return ResponseEntity.noContent().build();
		
	}
	
	
	// ====================================================================================================
	// 4. BUSCAR ENTREGADOR POR ID
	// ====================================================================================================
	@GetMapping("/{id}")
	public ResponseEntity<EntregadorResponse> buscarPorId(@PathVariable Integer id){
		
		EntregadorResponse response = entregadorService.buscarEntregadorPorIdResponse(id);
		
		return ResponseEntity.ok(response);
		
	}

	
	// ====================================================================================================
	// 5. BUSCAR ENTREGADOR POR NOME E LISTA 
	// ====================================================================================================
	@GetMapping
	public ResponseEntity<Page<EntregadorResponse>> listarPorNome(@RequestParam(required = false) String nome, Pageable pageable){
		
		Page<EntregadorResponse> paginaResultados;
		
		if(nome != null) {
			paginaResultados = entregadorService.buscaEntregadorPorNomeResponse(nome, pageable);
		}else {
			paginaResultados = entregadorService.listarTodosEntregadoresResponse(pageable);
		}
		return ResponseEntity.ok(paginaResultados);
	}

}
