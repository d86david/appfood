package com.dsys.appfood.controller;

import com.dsys.appfood.dto.request.ClienteRequest;
import com.dsys.appfood.dto.response.ClienteResponse;
import com.dsys.appfood.service.ClienteService;

import jakarta.validation.Valid;

import java.net.URI;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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

/**
 * Controlador responsável por expor os endpoints da entidade Cliente
 * 
 * Responsabilidades: - Receber requisições HTTP. - Validar os dados de entrada
 * com @Valid. - Chamar os métodos do ClienteService. - Retornar respostas HTTP
 * adequadas (201 Created, 200 OK, 204 No Content).
 * 
 * CAMADAS ENVOLVIDAS: - Controller (esta classe) -> recebe DTOs Request. -
 * Service (ClienteService) -> contém regras de negócio - Repository
 * (ClienteRepository) -> acesso ao banco de dados.
 * 
 * PADRÃO UTILIZADO: - Records para DTOs (imutáveis e modernos) - Injeção de
 * dependencia via construtor (final) - Métodos de fábrica "from" nos Responses
 * para converter Entidade -> DTO
 */

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

	private final ClienteService clienteService;

	public ClienteController(ClienteService clienteService) {
		this.clienteService = clienteService;
	}

	// ====================================================================================================
	// 1. CADASTRAR CLIENTE COMPLETO
	// ====================================================================================================
	@PostMapping
	public ResponseEntity<ClienteResponse> cadastrarCompleto(@RequestBody @Valid ClienteRequest request,
			UriComponentsBuilder uriBuilder) {

		// 1. Chamar o Service para executar as regras de negócio e salvar
		ClienteResponse response = clienteService.cadastrarClienteResponse(request);

		// 2. Retornar o código 201 (Created) e a URL do novo recurso
		URI uri = uriBuilder.path("/api/clientes/{id}").buildAndExpand(response.id()).toUri();

		// 3. Devolver o DTO de Saída (Response)
		return ResponseEntity.created(uri).body(response);

	}

	// ====================================================================================================
	// 2. CADASTRAR CLIENTE RÁPIDO
	// ====================================================================================================
	@PostMapping("/rapido")
	public ResponseEntity<ClienteResponse> cadastrarRapido(@RequestBody @Valid ClienteRequest request,
			UriComponentsBuilder uriBuilder){
		
		// 1. Chamar o Service para executar as regras de negócio e salvar
		ClienteResponse response = clienteService.cadastrarClienteRapidoResponse(request);
				
		// 2. Retornar o código 201 (Created) e a URL do novo recurso
		URI uri = uriBuilder.path("/api/clientes/{id}").buildAndExpand(response.id()).toUri();
				
		// 3. Devolver o DTO de Saída (Response)
		return ResponseEntity.created(uri).body(response);
	}
	
	// ====================================================================================================
	// 3. EDITAR CLIENTE
	// ====================================================================================================
	@PutMapping("/{id}")
	public ResponseEntity<ClienteResponse> atualizar (@PathVariable Integer id, 
			@RequestBody @Valid ClienteRequest request){
		
		ClienteResponse response = clienteService.editarClienteResponse(id, request);
		
		return ResponseEntity.ok(response);
		
	}
	
	// ====================================================================================================
	// 4. ATIVAR / INATIVAR CLIENTE
	// ====================================================================================================
	@PatchMapping("/{id}/status")
	public ResponseEntity<Void> alterarStatus(@PathVariable Integer id,
			@RequestParam Boolean ativo ){
		clienteService.alterarStatusCliente(id, ativo);
		
		return ResponseEntity.noContent().build();
	}
	
	// ====================================================================================================
	// 5. BUSCAR CLIENTE POR ID
	// ====================================================================================================
	@GetMapping("/{id}")
    public ResponseEntity<ClienteResponse> buscarPorId(@PathVariable Integer id) {
        ClienteResponse response = clienteService.buscarClientePorIdResponse(id);
        return ResponseEntity.ok(response);
    }
	
	// ====================================================================================================
	// 6. BUSCAR CLIENTES (COM FILTROS E PAGINAÇÃO)
	// ====================================================================================================
	/**
     * Endpoint de busca com filtros.
     * Regras:
     * - Se informar "telefone", busca exata e retorna UM cliente (único).
     * - Se informar "nome", busca por parte do nome (case insensitive) e retorna página.
     * - Se não informar nada, lista todos os clientes com paginação.
     *
     * CONCEITO: Este endpoint é um exemplo de "overloading" de funcionalidade
     * em um único endpoint GET. O Service é responsável por decidir qual método chamar.
     * O uso de Pageable permite controlar tamanho da página e ordenação via URL.
     * Exemplo: /api/clientes?nome=Joao&size=10&page=0&sort=nome,asc
     *
     * @param telefone Filtro exato (opcional).
     * @param nome     Filtro parcial (opcional).
     * @param pageable Parâmetros de paginação (size, page, sort).
     * @return Página de ClienteResponse.
     *
     * OBS: Evitado usar mais de um filtro forte simultaneamente para não complicar a query.
     * Aqui, o telefone tem prioridade caso seja informado.
     */
	@GetMapping
    public ResponseEntity<Page<ClienteResponse>> buscar(
            @RequestParam(required = false) String telefone,
            @RequestParam(required = false) String nome,
            @PageableDefault(size = 20, sort = "nome") Pageable pageable
    ) {
        Page<ClienteResponse> paginaResultados;

        // Prioridade: se tem telefone, busca exata (UNICO resultado, mas Pageable suporta)
        if (telefone != null && !telefone.isBlank()) {
            // Atenção: buscarClientePeloTelefoneResponse retorna UM, mas precisamos de Page.
            // Vamos adaptar: convertemos o único resultado em uma página.
            ClienteResponse cliente = clienteService.buscarClientePeloTelefoneResponse(telefone);
            // Transforma em Page (usando List.of para criar uma página de 1 elemento)
            paginaResultados = new PageImpl<>(List.of(cliente), pageable,1);
        } else if (nome != null && !nome.isBlank()) {
            paginaResultados = clienteService.buscarClientePeloNomeResponse(nome, pageable);
        } else {
            paginaResultados = clienteService.listarTodosClientesResponse(pageable);
        }

        return ResponseEntity.ok(paginaResultados);
    }
}
