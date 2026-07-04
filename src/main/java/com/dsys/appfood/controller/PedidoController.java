package com.dsys.appfood.controller;

import com.dsys.appfood.domain.enums.StatusPedido;
import com.dsys.appfood.dto.request.AdicionarItemRequest;
import com.dsys.appfood.dto.request.AdicionarSaborRequest;
import com.dsys.appfood.dto.request.BordaItemRequest;
import com.dsys.appfood.dto.request.CancelarPedidoRequest;
import com.dsys.appfood.dto.request.CustomizacaoRequest;
import com.dsys.appfood.dto.request.PedidoRequest;
import com.dsys.appfood.dto.request.ReabrirPedidoRequest;
import com.dsys.appfood.dto.request.StatusPedidoRequest;
import com.dsys.appfood.dto.request.VincularEntregadorRequest;
import com.dsys.appfood.dto.response.PedidoResponse;
import com.dsys.appfood.dto.response.PedidoResumoResponse;
import com.dsys.appfood.dto.response.StatusPedidoHistoricoResponse;
import com.dsys.appfood.service.PedidoService;
import com.dsys.appfood.service.StatusPedidoHistoricoService;

import jakarta.validation.Valid;

import java.net.URI;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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

/**
 * ======================================================================
 *  PEDIDO CONTROLLER - O CORAÇÃO DO SISTEMA
 * ======================================================================
 * 
 * Este Controller é responsavel por expor TODAS as operações relacionadas
 * ao ciclo de vida de um pedido, desde a sua criação até a sua finalização.
 * 
 * FLUXO COMPLETO DE UM PEDIDO
 * 
 *   1. INICIAR PEDIDO      			-> POST   /api/pedidos
 *   2. ADICIONAR ITEM      			-> POST   /api/pedidos/{id}/itens
 *   3. ADICIONAR SABOR     			-> POST   /api/pedidos/{id}/itens/{itemId}/sabores
 *   4. CUSTOMIZAR        				-> POST   /api/pedidos/{id}/customizacoes
 *   5. ADICIONAR BORDA     			-> POST   /api/pedidos/{id}/bordas
 *   6. REMOVER INGREDIENTE DO SABOR	-> DELETE /api/{pedidoId}/customizacoes/{itemId}/{subItemId}/{ingredenteId}
 *   7. REMOVER BORDA A UM ITEM			-> DELETE /api/{pedidoId}/bordas/{itemId}/{bordaId}
 *   8. VINCULAR ENTREGADOR 			-> PUT    /api/pedidos/{id}/entregador
 *   9. MUDAR STATUS        			-> PATCH  /api/pedidos/{id}/status
 *  10. FINALIZAR PEDIDO    			-> POST   /api/pedidos/{id}/finalizar
 *  11. CANCELAR PEDIDO     			-> POST   /api/pedidos/{id}/cancelar
 *  12. REABRIR PEDIDO      			-> POST   /api/pedidos/{id}/reabrir
 *  13. CONSULTAR PEDIDO POR ID 		-> GET	  /api/{pedidoId}
 *  14. LISTAR PEDIDOS COM FILTROS		-> GET    /api/pedidos
 *  15. CONSULTAR HISTÓRICO				-> GET    /api/{pedidoId}/historico
 *  16. LISTAR PEDIDOS EM ABERTO        -> GET    /api/pedidos/abertos
 *  
 *  
 *  CONCEITOS RESTful APLICADOS
 *  - Recursos identificados por URIs: /api/pedidos/{id}
 *  - Verbos HTTP corretos: POST (criar), PUT (substituir), PATCH (atualizar parcial)
 *  - Status HTTP adequados: 201 Created, 200 OK, 204 no Content, 404 Not Found
 *  - Sub-recusrsos: /pedidos/{id}/itens, /pedidos/{id}/status
 * 
 * SEPARAÇÃO DE RESPONSABILIDADES:
 *  - Controller: Recebe requisições, valida, chama Service, monta resposta HTTP.
 *  - Service: Contém TODA a lógica de negócio, transações, regras.
 *  - Repository: Acesso ao banco de dados.
 */
@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {
	
	private final StatusPedidoHistoricoService historicoService;
	private final PedidoService pedidoService;

	public PedidoController(PedidoService pedidoService, StatusPedidoHistoricoService historicoService) {
		this.pedidoService = pedidoService;
		this.historicoService = historicoService;
	}
	
	
	// ====================================================================================================
    // 1. INICIAR / CRIAR UM PEDIDO
	// ====================================================================================================
	/**
	 * Cria um novo pedido no sistema
	 * 
	 * FLUXO: 
	 * 1. O operador informa o cliente (ou não, para balcão anônimo)
	 * 2. Define o tipo: MESA, BALCÃO ou ENTREGA
	 * 3. Se for MESA, informa o numero da mesa (que será ocupada)
	 * 4. Se for BALCAO sem cliente, informa o nome para identificação
	 * 5. O pedido nasce com status PEDIDO_INICIADO
	 */
	@PostMapping
	public ResponseEntity<PedidoResponse> inciarPedido(
			@RequestBody @Valid PedidoRequest request,
			@RequestParam Integer operadorId,
			UriComponentsBuilder uriBuilder){
		
		// 1. Chamar o Service para executar as regras de negócio e salvar
		PedidoResponse response = pedidoService.iniciarPedidoResponse(request, operadorId);
		
		// 2. Retornar o código 201 (Created) e a URL do novo recurso
		URI uri = uriBuilder.path("/api/pedidos/{id}").buildAndExpand(response.id()).toUri();
		
		// 3. Devolver o DTO de Saída (Response)
		return ResponseEntity.created(uri).body(response);
	}
	
	// ====================================================================================================
    // 2. ADICIONAR ITEM AO PEDIDO
	// ====================================================================================================
	/**
	  * Adiciona um item (linha) ao pedido.
	 *
	 * O que é um ItemPedido?
	 * - Representa UMA unidade de consumo no pedido.
	 * - Exemplos: 1 Pizza, 1 Refrigerante, 1 Porção de Batata.
	 * - Cada item tem um TAMANHO (P, M, G ou "Unitário").
	 *
	 * ======================================================================
	 *  COMO ESTE ENDPOINT FUNCIONA PARA DIFERENTES TIPOS DE PRODUTO?
	 * ======================================================================
	 *
	 * 1. PRODUTOS SIMPLES (Bebidas, Porções, Sobremesas):
	 *    - Envie o campo "produtoId" com o ID do produto.
	 *    - O sistema criará o ItemPedido e já adicionará o produto como ÚNICO sabor.
	 *    - Exemplo: Coca-Cola 600ml.
	 *    - NÃO é necessário chamar o endpoint /sabores depois.
	 *
	 * 2. PRODUTOS PERSONALIZÁVEIS (Pizzas):
	 *    - Deixe o campo "produtoId" como null.
	 *    - O sistema criará APENAS o container (ItemPedido vazio).
	 *    - Depois, você DEVE chamar o endpoint /sabores para adicionar 1 ou mais sabores.
	 *    - Exemplo: Pizza Grande com Calabresa e Mussarela.
	 *
	 * ======================================================================
	 *  REGRAS DE VALIDAÇÃO (no Service)
	 * ======================================================================
	 * - Se produtoId for informado, mas a categoria do produto for "personalizável",
	 *   o sistema lançará uma exceção (pois esse produto deve ser adicionado via /sabores).
	 * - Se produtoId for null, o sistema assume que é uma pizza e cria o item vazio.
	 * - O pedido deve estar com status PEDIDO_INICIADO ou PENDENTE.
	 * - O tamanho deve existir e estar ativo.
	 */
	@PostMapping("/{pedidoId}/itens")
	public ResponseEntity<PedidoResponse> adicionarItem(
			@PathVariable Integer pedidoId,
			@RequestBody @Valid AdicionarItemRequest request
			){
		
		
		PedidoResponse response = pedidoService.adicionarItemAoPedidoResponse(pedidoId, request);
		
		return ResponseEntity.ok(response);
		
	}
	
	
	// ====================================================================================================
    // 3. ADICIONAR SABOR A UM ITEM
	// ====================================================================================================
	/**
	 * Adiciona um sabor a um item específico. 
	 * 
	 * IMPORTANTE: 
	 *  - Um ItemPedido pode ter 1,2 ou mais sabores.
	 *  - Cada sabor é um SubItemSabor
	 *  - O preço depende do tamanho da pizza (P, M, G).
	 */
	@PostMapping("/{pedidoId}/sabores")
	public ResponseEntity<PedidoResponse> adicionarSabor(
						@PathVariable Integer pedidoId, 
						@RequestBody @Valid AdicionarSaborRequest request){
		
		PedidoResponse response = pedidoService.adicionarSaborAoItemResponse(pedidoId, request);
		
		return ResponseEntity.ok(response);
		
	}
	
	// ====================================================================================================
    // 4. ADICIONAR CUSTOMIZAÇÃO (INGREDIENTE) A UM SABOR
	// ====================================================================================================
	/**
     * Adiciona uma customização a um sabor específico.
     *
     * TIPOS DE CUSTOMIZAÇÃO:
     * - ADICIONAL: Adiciona um ingrediente extra (ex: +Bacon, pago).
     * - REMOCAO: Remove um ingrediente (ex: sem cebola, grátis).
     *
     * CONCEITO: Customizações são registradas no nível do SABOR.
     * Isso permite que em uma pizza com 2 sabores, cada sabor tenha
     * customizações diferentes (ex: Calabresa sem cebola, Mussarela com bacon).
     */
	@PostMapping("/{pedidoId}/customizacoes")
	public ResponseEntity<PedidoResponse> adicionarCustomizacao(
			@PathVariable Integer pedidoId, 
			@RequestBody @Valid CustomizacaoRequest request){
		
		PedidoResponse response = pedidoService.adicionarCustomizacaoResponse(pedidoId, request);
		
		return ResponseEntity.ok(response);
		
	}
	
	// ====================================================================================================
    // 5. ADICIONAR BORDA A UM ITEM
	// ====================================================================================================
	/**
	 * Adicionar uma borda a um item (Pizza) inteiro
	 * 
	 *DIFERENÇA DA CUSTOMIZAÇÃO:
     * - A borda é aplicada ao ITEM inteiro, não a um sabor específico.
     * - Exemplo: Pizza Grande com borda de catupiry.
     * - O valor da borda é adicionado ao preço final do item.
	 * 
	 * CONCEITO: Customizações GLOBAIS (no item) vs LOCAIS (no sabor).
     * - Borda é global: afeta a pizza toda.
     * - Ingrediente extra pode ser local: afeta apenas aquele sabor.
	 */
	@PostMapping("/{pedidoId}/bordas")
	public ResponseEntity<PedidoResponse> adicionarBorda(@PathVariable Integer pedidoId, @RequestBody @Valid BordaItemRequest request){
		
		PedidoResponse response = pedidoService.adicionarBordaAoItemResponse(pedidoId, request);
		
		return ResponseEntity.ok(response);
		
	}
	
	// ====================================================================================================
    // 6. REMOVER INGREDIENTE DO SABOR
	// ====================================================================================================
	/**
	 * Remove uma customização (ingrediente adicional/remoção) de um sabor.
	 * 
	 * CONCEITO: DELETE é o verbo correto para operação de remoção.
	 * A URI identifica exatamente qual customização será removida 
	 */
	@DeleteMapping("/{pedidoId}/customizacoes/{itemId}/{subItemId}/{ingredenteId}")
	public ResponseEntity<PedidoResponse> removerCustomizacao (
			@PathVariable Integer pedidoId,
			@PathVariable Integer itemId,
			@PathVariable Integer subItemId,
			@PathVariable Integer ingredienteId
			){
		
		// O operadorId pode ser obtido do contexto de segurança,
	    // mas para simplificar, passamos null ou um ID fixo em desenvolvimento.
	    // Como o método não usa operadorId, podemos passar null ou buscar do token.
		PedidoResponse response = pedidoService.removerCustomizacaoResponse(
				pedidoId, 
				itemId, 
				subItemId, 
				ingredienteId);
		
		return ResponseEntity.ok(response);
		
	}
	
	// ====================================================================================================
    // 7. REMOVER BORDA A UM ITEM
	// ====================================================================================================
	/**
	 * Remove uma borda de um item (pizza) específico.
	 *
	 * CONCEITO: A operação de remoção segue o mesmo padrão do DELETE.
	 */
	@DeleteMapping("/{pedidoId}/bordas/{itemId}/{bordaId}")
	public ResponseEntity<PedidoResponse> removerBordaDoItem(
			@PathVariable Integer pedidoId,
	        @PathVariable Integer itemId,
	        @PathVariable Integer bordaId){
		
		PedidoResponse response = pedidoService.removerBordaDoItemResponse(pedidoId, itemId, bordaId);
		
		return ResponseEntity.ok(response);
		
	}
	
	// ====================================================================================================
    // 8. VINCULAR ENTREGADOR AO PEDIDO
	// ====================================================================================================
	/**
	 * Vincula um entregador a um pedido do tipo ENTREGA 
	 * 
	 * QUANDO USAR: 
	 *  - Apenas o pedido ficar PRONTO na cozinha. 
	 *  - Antes de enviar para entrega (status SAIU_PARA_ENTREGA).
	 *  
	 *  REGRAS: 
	 *   - Apenas pedidos do tipo ENTREGA podem ter entregador.
	 *   - O entregador deve estar ATIVO.
	 *   - O pedido deve estar com status PRONTO.
	 *   
	 * CONCEITO: Este é um PUT porque estamos substituindo a relação
     * entregador-pedido (atualizando um campo específico).
	 * 
	 */
	@PutMapping("/{pedidoId}/entregador")
	public ResponseEntity<PedidoResponse> vincularEntregador (
				@PathVariable Integer pedidoId, 
				@RequestBody @Valid VincularEntregadorRequest request,
				@RequestParam Integer operadorId){
		PedidoResponse response = pedidoService.vincularEntregadorResponse(pedidoId, request, operadorId);
		
		return ResponseEntity.ok(response);
		
	}
	
	// ====================================================================================================
    // 9. MUDAR STATUS DO PEDIDO
	// ====================================================================================================
	/**
	 * Altera o status de um pedido 
	 * 
	 * FLUXO DE STATUS (Ordem natural) 
	 * PEDIDO_INICIADO -> PENDENTE -> EM_PREPARACAO -> PRONTO -> SAIU_PARA_ENTREGA -> FINALIZADO
	 * STATUS ESPECIAIS:
     *  - CANCELADO: Pode ser aplicado em qualquer momento (com autorização).
     *  
     * CONCEITO: PATCH é o verbo correto para atualizações parciais.
     * Estamos alterando APENAS o status, não o pedido inteiro.
     * Cada mudança de status gera um registro no histórico (StatusPedidoHistorico).
	 * 
	 */
	@PatchMapping("/{pedidoId}/status")
	public ResponseEntity<PedidoResponse> mudarStatus(
			@PathVariable Integer pedidoId,
			@RequestBody @Valid StatusPedidoRequest request,
			@RequestParam Integer operadorId){
		
		PedidoResponse response = pedidoService.mudarStatusResponse(pedidoId, request, operadorId);
		
		return ResponseEntity.ok(response);
		
	}
	
	// ====================================================================================================
    // 10. FINALIZAR PEDIDO
	// ====================================================================================================
	/**
	 * Finaliza um pedido (encerra o ciclo).
	 * 
	 * REGRAS PARA FINALIZAÇÃO:
     * 1. O pedido deve estar totalmente PAGO.
     * 2. O status deve ser PRONTO ou SAIU_PARA_ENTREGA.
     * 3. Se for ENTREGA, deve ter um entregador vinculado.
     * 4. Se for MESA, deve ter número da mesa informado.
     * 
     * AÇÕES REALIZADAS:
     * - Define a data/hora de finalização.
     * - Altera o status para FINALIZADO.
     * - Libera a mesa (se for MESA).
     * 
     * CONCEITO: A finalização é um evento importante e irreversível.
     * Todas as regras de negócio estão encapsuladas no Service e na Model.
     * O Controller apenas orquestra a chamada.
	 */
	@PostMapping("/{pedidoId}/finalizar")
	public ResponseEntity<PedidoResponse> finalizar(
			@PathVariable Integer pedidoId,
            @RequestParam Integer operadorId
			){
		
		PedidoResponse response = pedidoService.finalizarPedidoResponse(pedidoId, operadorId);
		
		return ResponseEntity.ok(response);
		
	}
	
	// ====================================================================================================
    // 11. CANCELAR PEDIDO
	// ====================================================================================================
	/**
     * Cancela um pedido (requer autorização de gerente).
     *
     * REGRAS PARA CANCELAMENTO:
     * 1. Se o pedido já tiver pagamentos, exige autorização de gerente.
     * 2. O motivo do cancelamento é OBRIGATÓRIO.
     * 3. Se for MESA, a mesa é liberada automaticamente.
     * 4. O status muda para CANCELADO.
     *
     * CONCEITO: Segregação de responsabilidades.
     * - Operador solicita o cancelamento (passa o motivo).
     * - Gerente autoriza (passa o ID).
     * - O Service valida se o gerente realmente é um gerente.
     * - O motivo é armazenado para auditoria.
     */
	@PostMapping("/{pedidoId}/cancelar")
	public ResponseEntity<PedidoResponse> cancelarPedido(
			@PathVariable Integer pedidoId,
			@RequestBody @Valid CancelarPedidoRequest request,
			@RequestParam Integer operadorId
			){
		
		PedidoResponse response = pedidoService.cancelarPedidoResponse(pedidoId, request, operadorId);
		
		return ResponseEntity.ok(response);
		
	}
	
	// ====================================================================================================
    // 12. REABRIR PEDIDO CANCELADO
	// ====================================================================================================
	 /**
     * Reabre um pedido que foi cancelado (requer autorização de gerente).
     *
     * QUANDO USAR:
     * - Quando um pedido foi cancelado por engano.
     * - O gerente pode reverter o cancelamento.
     *
     * REGRAS:
     * - O pedido deve estar com status CANCELADO.
     * - Apenas gerentes podem reabrir.
     * - Se for MESA, a mesa deve estar livre para ser ocupada novamente.
     *
     * CONCEITO: Rollback de operação.
     * Reabrir um pedido é uma operação administrativa que restaura
     * o pedido ao estado anterior ao cancelamento.
     */
	@PostMapping("/{pedidoId}/reabrir")
	public ResponseEntity<PedidoResponse> reabrirPedido(
			@PathVariable Integer pedidoId,
            @RequestBody @Valid ReabrirPedidoRequest request,
            @RequestParam Integer operadorId
			){
		
		PedidoResponse response = pedidoService.reabrirPedidoCanceladoResponse(pedidoId, request, operadorId);
		
		return ResponseEntity.ok(response);
		
	}
	
	// ====================================================================================================
    // 13. CONSULTAR PEDIDO POR ID (DETALHADO)
	// ====================================================================================================
	/**
     * Busca um pedido pelo ID com todos os detalhes.
     *
     * CONCEITO: Esta operação carrega todos os relacionamentos
     * (itens, sabores, customizações, pagamentos) em uma única consulta.
     * O Service usa @Transactional(readOnly = true) para otimização.
     */
	@GetMapping("/{pedidoId}")
	public ResponseEntity<PedidoResponse> buscarPorId(@PathVariable Integer pedidoId){
		
		PedidoResponse response = pedidoService.buscarPorIdResponse(pedidoId);
		
		return ResponseEntity.ok(response);
	}
	
	// =============================================================
	// 14. LISTAR PEDIDOS COM FILTROS (RESUMO)
	// =============================================================
	/**
     * Lista pedidos com filtros e paginação (apenas resumo).
     *
     * FILTROS DISPONÍVEIS:
     * - status: Filtra por status específico (ex: PENDENTE).
     * - tipo: Filtra por tipo (MESA, BALCAO, ENTREGA).
     *
     * CONCEITO: Esta listagem retorna um RESUMO (menos dados).
     * Para ver os detalhes completos, o cliente deve usar GET /pedidos/{id}.
     * Isso melhora a performance e evita sobrecarga de dados desnecessários.
     */
    @GetMapping
    public ResponseEntity<Page<PedidoResumoResponse>> listarPedidos(
            @RequestParam(required = false) StatusPedido status,
            @RequestParam(required = false) com.dsys.appfood.domain.enums.TipoPedido tipo,
            @PageableDefault(size = 20, sort = "dtHoraAbertura,desc") Pageable pageable
    ) {

        Page<PedidoResumoResponse> paginaResultados = pedidoService.listarPedidosResponse(status, tipo, pageable);
        return ResponseEntity.ok(paginaResultados);
    }
    
    
    // =============================================================
    // 15. CONSULTAR HISTÓRICO DE STATUS DO PEDIDO
    // =============================================================
    /**
     * Retorna o histórico completo de mudanças de status de um pedido.
     *
     * O HISTÓRICO REGISTRA:
     * - Status (PENDENTE, EM_PREPARACAO, etc.)
     * - Data/hora da mudança.
     * - Usuário que realizou a mudança.
     *
     * CONCEITO: Auditoria e rastreabilidade.
     * O histórico permite saber exatamente quando e quem alterou o status
     * do pedido, fundamental para análises e resolução de problemas.
     */
    @GetMapping("/{pedidoId}/historico")
    public ResponseEntity<List<StatusPedidoHistoricoResponse>> historicoStatus (@PathVariable Integer pedidoId){
    	
    	List<StatusPedidoHistoricoResponse> historico = historicoService.historicoDoPedidoResponse(pedidoId);
    	
    	return ResponseEntity.ok(historico);
    	
    }
    
    
    // =============================================================
    // 16. LISTAR PEDIDOS EM ABERTO (GRADE DE PEDIDOS)
    // =============================================================
    /**
     * Lista todos os pedidos em aberto (não finalizados e não cancelados).
     * 
     * PEDIDOS EM ABERTO INCLUEM:
     * - PEDIDO_INICIADO
     * - PENDENTE
     * - EM_PREPARACAO
     * - PRONTO
     * - SAIU_PARA_ENTREGA
     * 
     * EXCLUI:
     * - FINALIZADO
     * - CANCELADO
     * 
     * CONCEITO: Esta listagem é a "grade de pedidos" que o atendente vê.
     * Útil para monitorar o que está em andamento na cozinha e no balcão.
     * Ordenação padrão: mais recentes primeiro.
     */
    @GetMapping("/abertos")
    public ResponseEntity<Page<PedidoResumoResponse>> listarPedidosAbertos(
    		@PageableDefault(size = 20  ) Pageable pageable // sem o argumento sort para ser ecolhido no front a ordenação, 
    		){                                              // o front deverá enviar na URL ?sort=dtHoraAbertura,desc
    	
    	Page<PedidoResumoResponse> paginaResultados = pedidoService.listarPedidosAbertosResponse(pageable);
    	
    	return ResponseEntity.ok(paginaResultados);
    	
    }
}




















