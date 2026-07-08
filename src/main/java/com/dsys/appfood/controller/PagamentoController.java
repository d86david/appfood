package com.dsys.appfood.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dsys.appfood.dto.request.EstornoPagamentoRequest;
import com.dsys.appfood.dto.request.PagamentoDinheiroRequest;
import com.dsys.appfood.dto.request.PagamentoMultiploRequest;
import com.dsys.appfood.dto.request.PagamentoRequest;
import com.dsys.appfood.dto.response.PagamentoComTrocoResponse;
import com.dsys.appfood.dto.response.PagamentoResponse;
import com.dsys.appfood.service.PagamentoService;

import jakarta.validation.Valid;

/**
 * ======================================================================
 *  PAGAMENTO CONTROLLER - GERENCIAMENTO FINANCEIRO DO PEDIDO
 * ======================================================================
 *
 * Este Controller é responsável por EXPOR todas as operações relacionadas
 * a pagamentos, fechando o ciclo financeiro do pedido.
 *
 * FLUXO COMPLETO DE PAGAMENTO:
 *
 *   1. REGISTRAR PAGAMENTO ÚNICO   -> POST /api/pagamentos
 *   2. REGISTRAR PAGAMENTO MÚLTIPLO -> POST /api/pagamentos/multiplos
 *   3. PROCESSAR PAGAMENTO DINHEIRO -> POST /api/pagamentos/dinheiro
 *   4. CALCULAR TROCO              -> GET  /api/pagamentos/troco/{pedidoId}
 *   5. ESTORNAR PAGAMENTO          -> POST /api/pagamentos/{pagamentoId}/estornar
 *   6. LISTAR PAGAMENTOS DO PEDIDO -> GET  /api/pagamentos/pedido/{pedidoId}
 *   7. VERIFICAR PEDIDO QUITADO    -> GET  /api/pagamentos/pedido/{pedidoId}/quitado
 *
 * CONCEITOS RESTful APLICADOS:
 *   - Recursos identificados por URIs: /api/pagamentos/{id}
 *   - Verbos HTTP corretos: POST (criar), GET (consultar)
 *   - Status HTTP adequados: 201 Created, 200 OK, 204 No Content, 404 Not Found
 *
 * SEPARAÇÃO DE RESPONSABILIDADES:
 *   - Controller: Recebe requisições, valida, chama Service, monta resposta HTTP.
 *   - Service: Contém TODA a lógica de negócio, transações, regras.
 *   - Repository: Acesso ao banco de dados.
 *
 * RELACIONAMENTOS:
 *   - Pagamento -> Pedido (muitos para um)
 *   - Pagamento -> Caixa (muitos para um)
 *   - Pagamento -> Usuario (operador)
 *   - Pagamento -> ContaCorrente (via ConfiguracaoPagamento)
 */
@RestController
@RequestMapping("/api/pagamentos")
public class PagamentoController {

	private final PagamentoService pagamentoService;

	public PagamentoController (PagamentoService pagamentoService) {

		this.pagamentoService = pagamentoService;

	}

	// ====================================================================================================
	// 1. REGISTRAR PAGAMENTO ÚNICO
	// ====================================================================================================
	/**
     * Registra um pagamento único para um pedido.
     *
     * FLUXO:
     * 1. O operador informa o pedido, caixa, forma e valor.
     * 2. O sistema valida se o pedido está aberto e se o valor é válido.
     * 3. Se for DINHEIRO, o valor é registrado no caixa.
     * 4. Se for outra forma (PIX, CREDITO), é registrado na ContaCorrente correspondente.
     * 5. Se o pedido ficar quitado, o status é alterado para FINALIZADO (se já estiver PRONTO).
     *
     * CONCEITO: O caixaId e operadorId são QueryParams porque são contexto
     * da operação, não fazem parte do recurso "pagamento" em si.
     * Usamos POST porque cada pagamento é um evento único e não idempotente.
     */
	@PostMapping
	public ResponseEntity<PagamentoResponse> registrarPagamento (
			@RequestParam Integer pedidoId,
			@RequestParam Integer caixaId,
			@RequestParam Integer operadorId,
			@RequestBody @Valid PagamentoRequest request
			){

		PagamentoResponse response = pagamentoService.registrarPagamentoResponse(pedidoId, caixaId, operadorId, request);

		return ResponseEntity.status(201).body(response);

	}

	// ====================================================================================================
	// 2. REGISTRAR PAGAMENTO MÚLTIPLO
	// ====================================================================================================
	/**
     * Registra MÚLTIPLOS pagamentos para um pedido em uma única requisição.
     *
     * QUANDO USAR:
     * - Cliente paga parte em dinheiro e parte no cartão.
     * - Cliente usa voucher + dinheiro.
     * - Qualquer combinação de formas de pagamento.
     *
     * VANTAGENS:
     * - Reduz o número de requisições HTTP.
     * - Garante atomicidade: todos os pagamentos são registrados ou nenhum.
     * - Melhor experiência para o operador.
     *
     * CONCEITO: Transação atômica. Se um pagamento falhar, nenhum é registrado.
     * O Service garante isso via @Transactional.
     */
	@PostMapping("/multiplos")
	public ResponseEntity<List<PagamentoResponse>> registrarPagamentoMultiplo (

			@RequestParam Integer pedidoId,
			@RequestParam Integer caixaId,
			@RequestBody @Valid PagamentoMultiploRequest request

			){

		List<PagamentoResponse> response = pagamentoService.registrarPagamentoMultiploResponse(pedidoId, caixaId, request);

		return ResponseEntity.status(201).body(response);

	}

	// ====================================================================================================
	// 3. PROCESSAR PAGAMENTO EM DINHEIRO (COM TROCO)
	// ====================================================================================================
	/**
     * Processa um pagamento em DINHEIRO com cálculo automático de troco.
     *
     * FLUXO:
     * 1. O operador informa quanto o cliente entregou em dinheiro.
     * 2. O sistema calcula o troco automaticamente.
     * 3. Registra apenas o valor necessário para quitar o pedido.
     * 4. Retorna o troco para o operador.
     *
     *
     * CONCEITO: O troco NÃO entra no caixa como valor registrado.
     * Apenas o valor real devido é registrado. O troco é calculado e devolvido.
     */
	@PostMapping("/dinheiro")
	public ResponseEntity<PagamentoComTrocoResponse> processarPagamentoDinheiro(
			@RequestParam Integer pedidoId,
			@RequestParam Integer caixaId,
			@RequestBody @Valid PagamentoDinheiroRequest request
			){

		PagamentoComTrocoResponse response = pagamentoService.processarPagamentoDinheiroResponse(pedidoId, caixaId, request);

		return ResponseEntity.status(201).body(response);

	}

	// ====================================================================================================
	// 4. CALCULAR TROCO (APENAS CONSULTA)
	// ====================================================================================================
    /**
     * Calcula o troco para um pagamento em dinheiro SEM registrar o pagamento.
     *
     * QUANDO USAR:
     * - O operador quer saber antecipadamente quanto será o troco.
     * - Útil para preparar o troco antes de finalizar a venda.
     *
     * CONCEITO: Operação de LEITURA (readOnly). Não modifica o estado do sistema.
     * É uma consulta conveniente para o frontend.
     */
    @GetMapping("/troco/{pedidoId}")
    public ResponseEntity<BigDecimal> calcularTroco(
    		@PathVariable Integer pedidoId,
    		@RequestParam BigDecimal valorRecebido
    		){
    	BigDecimal troco = pagamentoService.calcularTroco(pedidoId, valorRecebido);

    	return ResponseEntity.ok(troco);
    }


	// ====================================================================================================
	// 5. ESTORNAR PAGAMENTO
	// ====================================================================================================
    /**
     * Estorna um pagamento já registrado (requer autorização de gerente).
     *
     * QUANDO USAR:
     * - Pagamento registrado por engano (ex: valor incorreto).
     * - Cliente desiste do pedido após pagar.
     * - Erro operacional.
     *
     * REGRAS:
     * - Apenas GERENTES podem estornar pagamentos.
     * - Não é possível estornar pagamento de pedido FINALIZADO.
     * - Para DINHEIRO: uma SAÍDA é registrada no caixa.
     * - Para outras formas: uma movimentação inversa é registrada na ContaCorrente.
     *
     * CONCEITO: Estorno é uma operação ADMINISTRATIVA, não uma exclusão.
     * O pagamento é removido da lista do pedido, mas mantemos rastro via auditoria.
     * O motivo é obrigatório para rastreabilidade.
     */
    @PostMapping("/{pagamentoId}/estornar")
    public ResponseEntity<Void> estornarPagamento (
    		@PathVariable Integer pagamentoId,
    		@RequestBody @Valid EstornoPagamentoRequest request
    		){
    	// 1. Chama o Service que executa o estorno
    	pagamentoService.estornarPagamento(
    			pagamentoId,
    			request.motivo(),
    			request.gerenteId()
    			);

    	// 2. Retorna 204 No Content (operação bem-sucedida, sem conteúdo para retornar)
        return ResponseEntity.noContent().build();
    }

	// ====================================================================================================
	// 6. LISTAR PAGAMENTOS DE UM PEDIDO
	// ====================================================================================================
    /**
     * Lista todos os pagamentos registrados para um pedido específico.
     *
     * CONCEITO: Pagamentos são um sub-recurso do Pedido.
     * Útil para exibir o resumo financeiro no frontend.
     */
    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<List<PagamentoResponse>> listarPagamentosDosPedido(@PathVariable Integer pedidoId){

    	List<PagamentoResponse> response = pagamentoService.listarPagamentosDoPedidoResponse(pedidoId);

    	return ResponseEntity.ok(response);

    }

	// ====================================================================================================
	// 7. VERIFICAR SE PEDIDO ESTÁ QUITADO
	// ====================================================================================================
    /**
     * Verifica se um pedido está totalmente quitado (pago).
     *
     * CONCEITO: Método de conveniência para o frontend.
     * Útil para habilitar/desabilitar o botão de finalizar pedido.
     */
    @GetMapping("/pedido/{pedidoId}/quitado")
    public ResponseEntity<Boolean> isPedidoQuitado(
            @PathVariable Integer pedidoId
    ) {
        boolean quitado = pagamentoService.isPedidoQuitado(pedidoId);
        return ResponseEntity.ok(quitado);
    }
}
