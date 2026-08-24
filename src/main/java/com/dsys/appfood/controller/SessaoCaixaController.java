package com.dsys.appfood.controller;



import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

import com.dsys.appfood.domain.model.SessaoCaixa;
import com.dsys.appfood.dto.request.SessaoCaixaAbrirRequest;
import com.dsys.appfood.dto.request.SessaoCaixaEstornoRequest;
import com.dsys.appfood.dto.request.SessaoCaixaFecharRequest;
import com.dsys.appfood.dto.request.SessaoCaixaSangriaRequest;
import com.dsys.appfood.dto.response.SessaoCaixaStatusResponse;
import com.dsys.appfood.dto.response.MovimentacaoCaixaResponse;
import com.dsys.appfood.dto.response.ResumoCaixaResponse;
import com.dsys.appfood.dto.response.SessaoCaixaResponse;
import com.dsys.appfood.service.SessaoCaixaService;
import com.dsys.appfood.service.MovimentacaoCaixaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/caixas")
public class SessaoCaixaController {


	private final MovimentacaoCaixaService movimentacaoCaixaService;
	private final SessaoCaixaService sessaoCaixaService;

	public SessaoCaixaController(SessaoCaixaService sessaoCaixaService, MovimentacaoCaixaService movimentacaoCaixaService) {

		this.sessaoCaixaService = sessaoCaixaService;
		this.movimentacaoCaixaService = movimentacaoCaixaService;

	}

	// =============================================================
    // 1. ABRIR CAIXA
    // =============================================================
	@PostMapping("/abertura")
	public ResponseEntity<SessaoCaixaResponse> abrirCaixa(@RequestBody @Valid SessaoCaixaAbrirRequest request){

		SessaoCaixaResponse response = sessaoCaixaService.abrirCaixaResponse(request);

		return ResponseEntity.status(HttpStatus.CREATED).body(response);

	}


	// =============================================================
    // 2. BUSCAR CAIXA ABERTO ATUAL
    // =============================================================
	@GetMapping("/aberto")
	public ResponseEntity<Page<SessaoCaixaStatusResponse>> buscarCaixasAbertos(Pageable pageable){

		Page<SessaoCaixaStatusResponse> paginaResultados = sessaoCaixaService.listarSessoesAbertasResponse(pageable);

			return ResponseEntity.ok(paginaResultados);
	}


	// =============================================================
    // 3. FECHAR CAIXA
    // =============================================================
	@PostMapping("/{caixaId}/fechamento")
	public ResponseEntity<SessaoCaixaResponse> fecharCaixa(@RequestBody @Valid SessaoCaixaFecharRequest request){

		SessaoCaixaResponse response = sessaoCaixaService.fecharCaixaResponse(request);

		return ResponseEntity.ok(response);
	}


	// =============================================================
    // 2. REALIZAR SANGRIA
    // =============================================================
	@PostMapping("/{caixaId}/sangrias")
	public ResponseEntity<MovimentacaoCaixaResponse> realizarSangria(@RequestBody @Valid SessaoCaixaSangriaRequest request){

		MovimentacaoCaixaResponse response = sessaoCaixaService.realizarSangriaResponse(request);

		return ResponseEntity.ok(response);
	}

	// =============================================================
    // 3. REGISTRAR ESTORNO
    // =============================================================
	@PostMapping("/movimentacoes/{movimentacaoId}/estorno")
    public ResponseEntity<MovimentacaoCaixaResponse> registrarEstorno(@PathVariable Integer movimentacaoId,
                                                                      @RequestBody @Valid SessaoCaixaEstornoRequest request) {

    	MovimentacaoCaixaResponse response = sessaoCaixaService.realizarEstornoResponse(movimentacaoId, request);

        return ResponseEntity.ok(response);
    }

	// =============================================================
    // 4. LISTAR MOVIMENTAÇÕES
    // =============================================================
    @GetMapping("/{caixaId}/movimentacoes")
    public ResponseEntity<List<MovimentacaoCaixaResponse>> listarMovimentacoes(
            @PathVariable SessaoCaixa sessao,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm") LocalDateTime inicio,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm") LocalDateTime fim) {


        List<MovimentacaoCaixaResponse> response = movimentacaoCaixaService.listarMovimentacoesResponse(sessao, inicio, fim);

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
