package com.dsys.appfood.controller;

import com.dsys.appfood.dto.request.CanalImpressaoRequest;
import com.dsys.appfood.dto.response.CanalImpressaoResponse;
import com.dsys.appfood.service.CanalImpressaoService;

import jakarta.validation.Valid;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api/canais-impressao")
public class CanalImpressaoController {
	
	
	private final CanalImpressaoService canalService;

	public CanalImpressaoController(CanalImpressaoService canalService) {
		
		this.canalService = canalService;
		
	}
	
	// ====================================================================================================
	// 1. CADASTRAR
	// ====================================================================================================
	@PostMapping
	public ResponseEntity<CanalImpressaoResponse> cadastrar(@RequestBody @Valid CanalImpressaoRequest request, 
															UriComponentsBuilder uriBuilder){
		
		CanalImpressaoResponse response = canalService.cadastrarResponse(request);
		
		URI uri = uriBuilder.path("/api/canais-impressao/{id}").buildAndExpand(response.id()).toUri();
		
		return ResponseEntity.created(uri).body(response);
		
	}
	
	
	// ====================================================================================================
	// 2. EDITAR
	// ====================================================================================================
	@PutMapping("/{id}")
    public ResponseEntity<CanalImpressaoResponse> editar(
            @PathVariable Integer id,
            @RequestBody @Valid CanalImpressaoRequest request) {
        return ResponseEntity.ok(canalService.editarResponse(id, request));
    }
	
	// ====================================================================================================
	// 3. LISTAR ATIVOS
	// ====================================================================================================
	 @GetMapping
	    public ResponseEntity<List<CanalImpressaoResponse>> listarAtivos(){
		 
		 return ResponseEntity.ok(canalService.listarAtivosResponse());
	 }
	
	// ====================================================================================================
	// 4. BUSCAR POR ID 
	// ====================================================================================================
	 @GetMapping("/{id}")
	    public ResponseEntity<CanalImpressaoResponse> buscarPorId(@PathVariable Integer id) {
	        return ResponseEntity.ok(CanalImpressaoResponse.from(canalService.buscarPorId(id)));
	    }
}
