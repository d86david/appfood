package com.dsys.appfood.controller;

import com.dsys.appfood.dto.request.MesaCadastroRequest;
import com.dsys.appfood.dto.request.MesaStatusRequest;
import com.dsys.appfood.dto.response.MesaResponse;
import com.dsys.appfood.service.MesaService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/mesas")
public class MesaController {

	private final MesaService mesaService;

	public MesaController(MesaService mesaService) {

		this.mesaService = mesaService;

	}

	// ====================================================================================================
	// 1. CADASTRAR NOVA MESA
	// ====================================================================================================
	@PostMapping
	public ResponseEntity<MesaResponse> cadastrar(@RequestBody @Valid MesaCadastroRequest request,
			UriComponentsBuilder uriBuilder) {

		// 1. Chamar o Service para executar as regras de negócio e salvar
		MesaResponse response = mesaService.cadastrarMesaResponse(request);

		// 2. Retornar o código 201 (Created) e a URL do novo recurso
		URI uri = uriBuilder.path("/api/mesas/{id}").buildAndExpand(response.id()).toUri();

		// 3. Devolver o DTO de Saída (Response)
		return ResponseEntity.created(uri).body(response);
	}
	
	// ====================================================================================================
	// 2. LIBERAR MESA
	// ====================================================================================================
	@PatchMapping("/{numeroMesa}/libera")
	public ResponseEntity<MesaResponse> liberarMesa(@PathVariable Integer numeroMesa){
		
		// 1. Chamar o Service para executar as regras de negócio e atualizar 
		MesaResponse response = mesaService.liberarMesaResponse(numeroMesa);
		
		// 2. Devolver o DTO de Saída (Response)
		return ResponseEntity.ok(response);
		
	}
	
	// ====================================================================================================
		// 3. OCUPAR MESA
		// ====================================================================================================
		@PatchMapping("/{numeroMesa}/ocupa")
		public ResponseEntity<MesaResponse> ocuparMesa(@PathVariable Integer numeroMesa){
			
			// 1. Chamar o Service para executar as regras de negócio e atualizar 
			MesaResponse response = mesaService.ocuparMesaResponse(numeroMesa);
			
			// 2. Devolver o DTO de Saída (Response)
			return ResponseEntity.ok(response);
			
		}

	// ====================================================================================================
	// 4. ATIVAR/INATIVAR MESA
	// ====================================================================================================
	@PatchMapping("/{numeroMesa}/status")
	public ResponseEntity<Void> atualizarStatus(@PathVariable Integer numeroMesa, 
			@RequestBody @Valid MesaStatusRequest request){
		mesaService.alterarStatusMesaResponse(numeroMesa, request);
		
		return ResponseEntity.noContent().build();
	}

	// ====================================================================================================
	// 5. BUSCAR MESA POR ID
	// ====================================================================================================
	@GetMapping("/{id}")
	public ResponseEntity<MesaResponse> buscaPorId(@PathVariable Integer id){
		
		MesaResponse response = mesaService.buscarMesaPorIdResponse(id);
		
		return ResponseEntity.ok(response);
		
	}
	
	// ====================================================================================================
	// 6. BUSCAR MESA POR NUMERO
	// ====================================================================================================
	@GetMapping("/numero/{numeroMesa}")
	public ResponseEntity<MesaResponse> buscaNumeroMesa(@PathVariable Integer numeroMesa){
		
		MesaResponse response = mesaService.buscarPorNumeroResponse(numeroMesa);
		
		return ResponseEntity.ok(response);
		
	}

	// ====================================================================================================
	// 7. LISTAR MESAS OCUPADAS 
	// ====================================================================================================
	@GetMapping("ocupadas")
	public ResponseEntity<Page<MesaResponse>> listarMesasOcupadas(Pageable pageable){
		
		return ResponseEntity.ok(mesaService.listarMesasOcupadasResponse(pageable));
		
	}
	
	
	// ====================================================================================================
	// 8. LISTAR MESAS LIVRES 
	// ====================================================================================================
	@GetMapping("livres")
	public ResponseEntity<Page<MesaResponse>> listarMesasLivres(Pageable pageable){
		
		return ResponseEntity.ok(mesaService.listarMesasLivresResponse(pageable));
		
	}	
	
	// ====================================================================================================
	// 9. LISTAR MESAS
	// ====================================================================================================
	@GetMapping
	public ResponseEntity<Page<MesaResponse>> listarMesas(Pageable pageable){
		
		return ResponseEntity.ok(mesaService.listarTodasAsMesaResponse(pageable));
		
	}

}
