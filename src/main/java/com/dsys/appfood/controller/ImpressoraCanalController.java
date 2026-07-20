package com.dsys.appfood.controller;

import com.dsys.appfood.dto.request.ImpressoraCanalRequest;
import com.dsys.appfood.dto.response.ImpressoraCanalResponse;
import com.dsys.appfood.service.ImpressoraCanalService;

import jakarta.validation.Valid;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/impressoras-canais")
public class ImpressoraCanalController {
	
	private final ImpressoraCanalService mapeamentoService;

	public ImpressoraCanalController (ImpressoraCanalService mapeamentoService) {
		
		this.mapeamentoService = mapeamentoService;
		
	}
	
	// ====================================================================================================
	// 1. VINCLUAR CANAL DE IMPRESSAO
	// ====================================================================================================
	@PostMapping
	public ResponseEntity<ImpressoraCanalResponse> vincular(@RequestBody @Valid ImpressoraCanalRequest request){
		
		// CONCEITO: Cria o vínculo N:N entre impressora e Canal
		return ResponseEntity.ok(mapeamentoService.vincularResponse(request));
	}
	
	// ====================================================================================================
	// 2. DESVINCLUAR CANAL DE IMPRESSAO
	// ====================================================================================================
	@DeleteMapping("/{impressoraId}/canais/{canalId}")
	public ResponseEntity<Void> desvincular(@PathVariable Integer impressoraId,
											@PathVariable Integer CanalId){
		
		// CONCEITO: DELETE aqui faz um "Soft Delete" (inativa o vínculo no banco), 
        // preservando o histórico sem quebrar integridade referencial.
		mapeamentoService.desvincular(impressoraId, CanalId);
		return ResponseEntity.noContent().build();
		
	}
	
	// ====================================================================================================
	// 3. LISTAR CANAIS DA IMPRESSAO
	// ====================================================================================================
	@GetMapping("/impressora/{impressoraId}")
	public ResponseEntity<List<ImpressoraCanalResponse>> listarCanaisDaImpressora (@PathVariable Integer impressoraId){
		
		return ResponseEntity.ok(mapeamentoService.listarPorImpressoraResponse(impressoraId));
		
	}
	
}
