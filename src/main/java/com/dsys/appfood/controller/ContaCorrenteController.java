package com.dsys.appfood.controller;

import com.dsys.appfood.domain.model.MovimentacaoContaCorrente;
import com.dsys.appfood.dto.request.ContaCorrenteEstornoRequest;
import com.dsys.appfood.dto.request.ContaCorrenteRequest;
import com.dsys.appfood.dto.request.ContaStatusRequest;
import com.dsys.appfood.dto.response.ContaCorrenteResponse;
import com.dsys.appfood.dto.response.MovimentacaoContaCorrenteResponse;
import com.dsys.appfood.dto.response.ResumoContaCorrenteResponse;
import com.dsys.appfood.service.ContaCorrenteService;
import com.dsys.appfood.service.MovimentacaoContaCorrenteService;

import jakarta.validation.Valid;

import java.net.URI;
import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
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
@RequestMapping("/api/contas")
public class ContaCorrenteController {

	private final MovimentacaoContaCorrenteService movimentacaoService;
	private final ContaCorrenteService contaCorrenteService;

	public ContaCorrenteController(ContaCorrenteService contaCorrenteService,
			MovimentacaoContaCorrenteService movimentacaoService) {

		this.contaCorrenteService = contaCorrenteService;
		this.movimentacaoService = movimentacaoService;
	}

	// ====================================================================================================
	// 1. CADASTRAR NOVA CONTA
	// ====================================================================================================
	@PostMapping
	public ResponseEntity<ContaCorrenteResponse> cadastrar(@RequestBody @Valid ContaCorrenteRequest request,
			UriComponentsBuilder uriBuilder) {

		ContaCorrenteResponse response = contaCorrenteService.cadastrarResponse(request);
		URI uri = uriBuilder.path("/contas/{id}").buildAndExpand(response.id()).toUri();
		return ResponseEntity.created(uri).body(response);

	}

	// ====================================================================================================
	// 2. EDITAR CONTA
	// ====================================================================================================
	@PutMapping("/{id}")
	public ResponseEntity<ContaCorrenteResponse> editar(@PathVariable Integer id,
			@RequestBody @Valid ContaCorrenteRequest request) {

		ContaCorrenteResponse response = contaCorrenteService.editarResponse(id, request);
		return ResponseEntity.ok(response);

	}

	// ====================================================================================================
	// 3. ATIVAR/INATIVAR CONTA
	// ====================================================================================================
	@PatchMapping("/{id}/status")
	public ResponseEntity<Void> atualizarStatus(@PathVariable Integer id,
			@RequestBody @Valid ContaStatusRequest request) {

		contaCorrenteService.alterarStatus(id, request.ativo());

		return ResponseEntity.noContent().build();

	}

	// ====================================================================================================
	// 4. REGISTRAR ESTORNO
	// ====================================================================================================
	@PostMapping("/movimentacoes/{movimentacaoId}/estorno")
	public ResponseEntity<MovimentacaoContaCorrenteResponse> estornarMovimentacao(@PathVariable Integer movimentacaoId,
			@RequestBody @Valid ContaCorrenteEstornoRequest request) {

		MovimentacaoContaCorrente estorno = contaCorrenteService.realizarEstornoInterno(movimentacaoId,
				request.gerenteId(), request.motivo());

		return ResponseEntity.ok(MovimentacaoContaCorrenteResponse.from(estorno));

	}

	// ====================================================================================================
	// 5. BUSCAR CONTA POR ID
	// ====================================================================================================
	@GetMapping("/{id}")
	public ResponseEntity<ContaCorrenteResponse> buscarPorId(@PathVariable Integer id) {
		return ResponseEntity.ok(contaCorrenteService.buscarPorIdResponse(id));
	}
	
	// ====================================================================================================
    // 6. OBTER RESUMO DA CONTA
    // ====================================================================================================
	 @GetMapping("/{contaId}/resumo")
	    public ResponseEntity<ResumoContaCorrenteResponse> resumo(
	            @PathVariable Integer contaId,
	            @RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm") LocalDateTime inicio,
	            @RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm") LocalDateTime fim) {
	        ResumoContaCorrenteResponse resumo;
	        if (inicio != null && fim != null) {
	            resumo = movimentacaoService.gerarResumoContaPeriodo(contaId, inicio, fim);
	        } else {
	            resumo = movimentacaoService.gerarResumoConta(contaId);
	        }
	        return ResponseEntity.ok(resumo);
	    }

	// ====================================================================================================
	// 7. EXTRATO
	// ====================================================================================================
	@GetMapping
	public ResponseEntity<Page<ContaCorrenteResponse>> listarAtivas(Pageable pageable) {
		return ResponseEntity.ok(contaCorrenteService.listarAtivasResponse(pageable));
	}
	
	@GetMapping("/{contaId}/movimentacoes")
	public ResponseEntity<Page<MovimentacaoContaCorrenteResponse>> extrato(
			@PathVariable Integer contaId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm") LocalDateTime inicio,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm") LocalDateTime fim,
            Pageable pageable){
		
		Page<MovimentacaoContaCorrenteResponse> paginaResultado = movimentacaoService
                .extratoPorContaResponse(contaId, inicio, fim, pageable);
		return ResponseEntity.ok(paginaResultado);
	}

}
