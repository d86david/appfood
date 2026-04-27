package com.dsys.appfood.controller;

import com.dsys.appfood.domain.model.Caixa;
import com.dsys.appfood.domain.model.MovimentacaoCaixa;
import com.dsys.appfood.dto.CaixaAbrirRequest;
import com.dsys.appfood.dto.CaixaResponse;
import com.dsys.appfood.dto.CaixaStatusResponse;
import com.dsys.appfood.dto.CaixaEstornoRequest;
import com.dsys.appfood.dto.CaixaFecharRequest;
import com.dsys.appfood.dto.MovimentacaoCaixaResponse;
import com.dsys.appfood.dto.ResumoCaixaResponse;
import com.dsys.appfood.dto.CaixaSangriaRequest;
import com.dsys.appfood.exception.NenhumCaixaAbertoException;
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
		Caixa caixa = caixaService.abrirCaixa(
				request.operadorId(), 
				request.loginGerente(), 
				request.senhaGerente(), 
				request.valorInicial()
				);
		return ResponseEntity.status(HttpStatus.CREATED).body(CaixaResponse.from(caixa));
	}
	

	// =============================================================
    // 2. BUSCAR CAIXA ABERTO ATUAL
    // =============================================================
	@GetMapping("/aberto")
	public ResponseEntity<CaixaStatusResponse> buscarCaixaAbertoAtual(){
		try {
			Caixa caixa = caixaService.buscarCaixaAbertoAtual();
			
			return ResponseEntity.ok(CaixaStatusResponse.deCaixaAberto(caixa));
			
		}catch(NenhumCaixaAbertoException e) {
			return ResponseEntity.ok(CaixaStatusResponse.vazio());
		}
	}
	

	// =============================================================
    // 3. FECHAR CAIXA 
    // =============================================================
	@PostMapping("/{caixaId}/fechamento")
	public ResponseEntity<CaixaResponse> fecharCaixa(@PathVariable Integer caixaId, 
														@RequestBody CaixaFecharRequest request){
		
		Caixa caixa = caixaService.fecharCaixa(caixaId, request.loginGerente(), request.senhaGerente());
		
		return ResponseEntity.ok(CaixaResponse.from(caixa));
	}
	

	// =============================================================
    // 2. REALIZAR SANGRIA
    // =============================================================
	@PostMapping("/{caixaId}/sangrias")
	public ResponseEntity<MovimentacaoCaixaResponse> realizarSangria(@PathVariable Integer caixaId, 
																	@RequestBody CaixaSangriaRequest request){
		
		MovimentacaoCaixa mov = caixaService.realizarSangria(
				caixaId, 
				request.loginGerente(), 
				request.senhaGerente(), 
				request.valor(), 
				request.motivo()
				);
		return ResponseEntity.status(HttpStatus.CREATED).body(MovimentacaoCaixaResponse.from(mov));
	}
	
	// =============================================================
    // 3. REGISTRAR ESTORNO
    // =============================================================
    @PostMapping("/{caixaId}/estornos")
    public ResponseEntity<MovimentacaoCaixaResponse> registrarEstorno(@PathVariable Integer caixaId,
                                                                      @RequestBody CaixaEstornoRequest request) {
        MovimentacaoCaixa mov = caixaService.registrarEstorno(
                caixaId,
                request.valor(),
                request.senhaGerente(),
                request.loginGerente(),
                request.motivo()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(MovimentacaoCaixaResponse.from(mov));
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
