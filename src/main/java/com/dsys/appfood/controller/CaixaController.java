package com.dsys.appfood.controller;


import com.dsys.appfood.domain.model.MovimentacaoCaixa;
import com.dsys.appfood.dto.CaixaAbrirRequest;
import com.dsys.appfood.dto.CaixaEstornoRequest;
import com.dsys.appfood.dto.CaixaResponse;
import com.dsys.appfood.dto.CaixaStatusResponse;
import com.dsys.appfood.dto.CaixaFecharRequest;
import com.dsys.appfood.dto.MovimentacaoCaixaResponse;
import com.dsys.appfood.dto.ResumoCaixaResponse;
import com.dsys.appfood.dto.CaixaSangriaRequest;
import com.dsys.appfood.service.CaixaService;
import com.dsys.appfood.service.MovimentacaoCaixaService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/caixas")
public class CaixaController {

	
	private final MovimentacaoCaixaService movimentacaoCaixaService;
	private final CaixaService caixaService;

	public CaixaController(CaixaService caixaService, MovimentacaoCaixaService movimentacaoCaixaService) {
		
		this.caixaService = caixaService;
		this.movimentacaoCaixaService = movimentacaoCaixaService;
		
	}
	
	// =============================================================
    // 1. ABRIR CAIXA
    // =============================================================
	@PostMapping("/abertura")
	public ResponseEntity<CaixaResponse> abrirCaixa(@RequestBody CaixaAbrirRequest request){
		
		CaixaResponse response = caixaService.abrirCaixaResponse(request);
		
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
		
	}
	

	// =============================================================
    // 2. BUSCAR CAIXA ABERTO ATUAL
    // =============================================================
	@GetMapping("/aberto")
	public ResponseEntity<CaixaStatusResponse> buscarCaixaAbertoAtual(){
	
			CaixaStatusResponse response = caixaService.buscarCaixaAbertoAtualResponse();
			
			return ResponseEntity.ok(response);
	}
	

	// =============================================================
    // 3. FECHAR CAIXA 
    // =============================================================
	@PostMapping("/{caixaId}/fechamento")
	public ResponseEntity<CaixaResponse> fecharCaixa(@PathVariable Integer caixaId, 
														@RequestBody CaixaFecharRequest request){
		
		CaixaResponse response = caixaService.fecharCaixaResponse(caixaId, request);
		
		return ResponseEntity.ok(response);
	}
	

	// =============================================================
    // 2. REALIZAR SANGRIA
    // =============================================================
	@PostMapping("/{caixaId}/sangrias")
	public ResponseEntity<MovimentacaoCaixaResponse> realizarSangria(@PathVariable Integer caixaId, 
																	@RequestBody CaixaSangriaRequest request){
		
		MovimentacaoCaixaResponse response = caixaService.realizarSangriaResponse(caixaId, request);
				
		return ResponseEntity.ok(response);
	}
	
	// =============================================================
    // 3. REGISTRAR ESTORNO
    // =============================================================
	@PostMapping("/movimentacoes/{movimentacaoId}/estorno")
    public ResponseEntity<MovimentacaoCaixaResponse> registrarEstorno(@PathVariable Integer movimentoId,
                                                                      @RequestBody CaixaEstornoRequest request) {
        
    	MovimentacaoCaixaResponse response = caixaService.realizarEstornoResponse(movimentoId, request);
        
        return ResponseEntity.ok(response);
    }
    
	// =============================================================
    // 4. LISTAR MOVIMENTAÇÕES
    // =============================================================
    @GetMapping("/{caixaId}/movimentacoes")
    public ResponseEntity<List<MovimentacaoCaixaResponse>> listarMovimentacoes(
            @PathVariable Integer caixaId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm") LocalDateTime inicio,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm") LocalDateTime fim) {
        
        List<MovimentacaoCaixa> movs;
        if (inicio != null && fim != null) {
            movs = movimentacaoCaixaService.extratoPorCaixaPeriodo(caixaId, inicio, fim);
        } else {
            movs = movimentacaoCaixaService.extratoPorCaixa(caixaId);
        }
        
        List<MovimentacaoCaixaResponse> response = movs.stream()
        		.map(MovimentacaoCaixaResponse::from)
        		.collect(Collectors.toList());
        return ResponseEntity.ok(response);
   }
    
	// =============================================================
    // 5. OBTER RESUMO DO CAIXA
    // =============================================================
    @GetMapping("/{caixaId}/resumo")
    public ResponseEntity<ResumoCaixaResponse> resumoCaixa(
            @PathVariable Integer caixaId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm") LocalDateTime inicio,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm") LocalDateTime fim) {
    	
    	ResumoCaixaResponse resumo;
    	
    	if(inicio != null && fim != null) {
            resumo = movimentacaoCaixaService.gerarResumoCaixaPeriodo(caixaId, inicio, fim);
        } else {
            resumo = movimentacaoCaixaService.gerarResumoCaixa(caixaId);
        }
    	return ResponseEntity.ok(resumo);
    }
	
}
