package com.dsys.appfood.controller;

import com.dsys.appfood.dto.request.ImpressoraRequest;
import com.dsys.appfood.dto.request.ImpressoraStatusRequest;
import com.dsys.appfood.dto.response.ImpressoraResponse;
import com.dsys.appfood.service.ImpressoraService;

import jakarta.validation.Valid;

import java.net.URI;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.util.UriComponentsBuilder;

@Controller
@RequestMapping("/api/impressoras")
public class ImpressoraController {
	
	


	private final ImpressoraService impressoraService;

	public ImpressoraController(ImpressoraService impressoraService) {
		
		this.impressoraService = impressoraService;
		
	}
	
	// ====================================================================================================
	// 1. CADASTRAR
	// ====================================================================================================
	@PostMapping
	public ResponseEntity<ImpressoraResponse> cadastrar(@RequestBody @Valid ImpressoraRequest request,
			UriComponentsBuilder uriBuilder){
		
		ImpressoraResponse response = impressoraService.cadastrarResponse(request);
		
		URI uri = uriBuilder.path("/api/impressoras/{id}").buildAndExpand(response.id()).toUri();
		
		return ResponseEntity.created(uri).body(response);
		
	}
	
	
	// ====================================================================================================
	// 2. EDITAR
	// ====================================================================================================
	@PutMapping("/{id}")
	public ResponseEntity<ImpressoraResponse> editar(@PathVariable Integer id,
			@RequestBody @Valid ImpressoraRequest request){
		
		ImpressoraResponse response = impressoraService.editarResponse(id, request);
		
		return ResponseEntity.ok(response);
		
	}
	
	// ====================================================================================================
	// 3. ALTERAR STATUS
	// ====================================================================================================
	@PatchMapping("/id/status")
	public ResponseEntity<Void> atualizarStatus(@PathVariable Integer id, 
			@RequestBody @Valid ImpressoraStatusRequest request){
		
		impressoraService.alterarStatusResponse(id, request);
		
		return ResponseEntity.noContent().build();
		
	}
	
	// ====================================================================================================
	// 4. BUSCAR POR ID
	// ====================================================================================================
	@GetMapping("/{id}")
    public ResponseEntity<ImpressoraResponse> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(impressoraService.buscarPorIdResponse(id));
    }

	// ====================================================================================================
	// 5. LISTAR
	// ====================================================================================================
    @GetMapping
    public ResponseEntity<Page<ImpressoraResponse>> listar(Pageable pageable) {
        return ResponseEntity.ok(impressoraService.listarTodasResponse(pageable));
    }

}
