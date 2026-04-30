package com.dsys.appfood.controller;

import com.dsys.appfood.service.BordaService;

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

import com.dsys.appfood.domain.model.Borda;
import com.dsys.appfood.dto.BordaRequest;
import com.dsys.appfood.dto.BordaResponse;


import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/bordas")
public class BordaController {

	private final BordaService bordaService;

	public BordaController(BordaService bordaService) {
		this.bordaService = bordaService;
	}

	// ====================================================================================================
	// 1. CADASTRAR NOVA BORDA
	// ====================================================================================================
	@PostMapping
	public ResponseEntity<BordaResponse> cadastrar(@RequestBody @Valid BordaRequest request,
			UriComponentsBuilder uriBuilder) {
		// 1. Chamar o Service para executar as regras de negócio e salvar
		Borda bordaSalva = bordaService.cadastrarBorda(request.nome(), request.valorAdicional());
		
		// 2. Retornar o código 201 (Created) e a URL do novo recurso
		URI uri = uriBuilder.path("bordas/{id}").buildAndExpand(bordaSalva.getId()).toUri();

		// 3. Devolver o DTO de Saída (Response)
		return ResponseEntity.created(uri).body(BordaResponse.from(bordaSalva));
	}

	// ====================================================================================================
	// 2. EDITAR BORDA
	// ====================================================================================================
	@PutMapping("/{id}")
	public ResponseEntity<BordaResponse> atualizar(@PathVariable Integer id, 
			@RequestBody @Valid BordaRequest request){
		
		Borda bordaAtualizada = bordaService.editarBorda(id, request.nome(), request.valorAdicional());
		
		return ResponseEntity.ok(BordaResponse.from(bordaAtualizada));
		
	}

	// ====================================================================================================
	// 3. EXCLUIR BORDA
	// ====================================================================================================
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> excluir(@PathVariable Integer id){
		
		bordaService.excluirBorda(id);
		
		return ResponseEntity.noContent().build();
	}

	// ====================================================================================================
	// 4. BUSCAR BORDA POR ID
	// ====================================================================================================
	@GetMapping("/{id}")
	public ResponseEntity<BordaResponse> buscarPorId(@PathVariable Integer id){
		
		Borda borda = bordaService.buscarBordaPorId(id);
		
		return ResponseEntity.ok(BordaResponse.from(borda));
	}

	// ====================================================================================================
	// 5. BUSCAR BORDA POR NOME
	// ====================================================================================================
	@GetMapping("/{nome}")
	public ResponseEntity<List<BordaResponse>> buscarPorNome(@RequestParam String nome){
		
		List<BordaResponse> bordas = bordaService.listarBordasPorNome(nome)
				.stream()
				.map(BordaResponse::from)
				.toList();
		
		return ResponseEntity.ok(bordas);
	}

	// ====================================================================================================
	// 6. LISTAR TODAS AS BORDAS
	// ====================================================================================================
	@GetMapping
	public ResponseEntity<List<BordaResponse>> listar(){
		List<BordaResponse> lista = bordaService.listarTodasBordas()
				.stream()
				.map(BordaResponse::from)
				.toList();
		
		return ResponseEntity.ok(lista);
	}
}
