package com.dsys.appfood.controller;

import com.dsys.appfood.dto.request.ComposicaoRequest;
import com.dsys.appfood.dto.request.ProdutoRequest;
import com.dsys.appfood.dto.response.ComposicaoPadraoResponse;
import com.dsys.appfood.dto.response.ProdutoResponse;
import com.dsys.appfood.service.ComposicaoPadraoService;
import com.dsys.appfood.service.ProdutoService;

import jakarta.validation.Valid;

import java.net.URI;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {
	
	private final ComposicaoPadraoService composicaoPadraoService;
	private final ProdutoService produtoService;
	

	public ProdutoController(ProdutoService produtoService, ComposicaoPadraoService composicaoPadraoService) {
		
		this.produtoService = produtoService;
		this.composicaoPadraoService = composicaoPadraoService;
		
	}
	
	// =============================================================
    // 1. CADASTRAR NOVO PRODUTO
    // =============================================================
	@PostMapping
	public ResponseEntity<ProdutoResponse> cadastrar(@RequestBody @Valid ProdutoRequest request, UriComponentsBuilder uriBuilder){
		
		ProdutoResponse response = produtoService.cadastrarProdutoResponse(request);
		
		URI uri = uriBuilder.path("/api/produtos/{id}").buildAndExpand(response.id()).toUri();
		
		return ResponseEntity.created(uri).body(response);
		
	}
	
	// =============================================================
    // 2. DEFINIR COMPOSIÇÃO PADRÃO
    // =============================================================
	@PostMapping("/{produtoId}/composicao")
	public ResponseEntity<ComposicaoPadraoResponse> definirComposicao(
			@PathVariable Integer produtoId,
			@RequestBody @Valid ComposicaoRequest request) {
		
		ComposicaoPadraoResponse response = composicaoPadraoService.definirComposicaoResponse(produtoId, request);
		
		return ResponseEntity.ok(response);
		
	}
	
	// =============================================================
    // 3. ADICIONAR INGREDIENTE NA COMPOSIÇÃO
    // =============================================================
	@PostMapping("/{produtoId}/composicao/ingredientes/{ingredienteId}")
	public ResponseEntity<ComposicaoPadraoResponse> adicionarIngrediente(
			@PathVariable Integer produtoId,
			@PathVariable Integer ingredienteId) {
		
		ComposicaoPadraoResponse response = composicaoPadraoService.adicionarIngredienteResponse(produtoId, ingredienteId);
		return ResponseEntity.ok(response);
		
	}
	
	// =============================================================
    // 4. EDITAR PRODUTO
    // =============================================================
	@PutMapping("/{id}")
	public ResponseEntity<ProdutoResponse> atualizar(@PathVariable Integer id, @RequestBody @Valid ProdutoRequest request){
		
		ProdutoResponse response = produtoService.editarProdutoResponse(id, request);
		return ResponseEntity.ok(response);
	}
	
	
	// =============================================================
    // 5. EXCLUIR PRODUTO
    // =============================================================
	@DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Integer id) {
        produtoService.excluirProduto(id);
        return ResponseEntity.noContent().build();
    }
	
	// =============================================================
    // 6. REMOVER INGREDIENTE DA COMPOSIÇÃO
    // =============================================================
	@DeleteMapping("/{produtoId}/composicao/ingredientes/{ingredienteId}")
	public ResponseEntity<Void> removerIngrediente(
			@PathVariable Integer produtoId,
			@PathVariable Integer ingredienteId){
		
		// O método removerIngredienteResponse retorna o DTO atualizado, mas podemos descartar ou retornar
		composicaoPadraoService.removerIngredienteResponse(produtoId, ingredienteId);
		
		// O ideal para DELETE é retornar 204 No Content
		return ResponseEntity.noContent().build();
		
	}
	
	
	// =============================================================
    // 7. BUSCAR PRODUTO POR ID
    // =============================================================
	@GetMapping("/{id}")
	public ResponseEntity<ProdutoResponse> buscarPorId(@PathVariable Integer id){
		ProdutoResponse response = produtoService.buscaProdutoResponsePorId(id);
		
		return ResponseEntity.ok(response);
		
	}
	
	// =============================================================
    // 8. BUSCAR COMPOSIÇÃO
    // =============================================================
	@GetMapping("/{produtoId}/composicao")
	public ResponseEntity<ComposicaoPadraoResponse> buscarComposicao(@PathVariable Integer produtoId) {
	    ComposicaoPadraoResponse response = composicaoPadraoService.buscarReceitaResponse(produtoId);
	    return ResponseEntity.ok(response);
	}
	
	// =============================================================
    // 9. OUTRAS BUSCAS DE PRODUTO
    // =============================================================
	@GetMapping
    public ResponseEntity<Page<ProdutoResponse>> listar(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) Integer categoriaId,
            Pageable pageable) {

        Page<ProdutoResponse> paginaResultados;
        
        if (nome != null) {
            paginaResultados = produtoService.buscarProdutoPorNomeResponse(nome, pageable);
        } else if (categoriaId != null) {
            paginaResultados = produtoService.listarProdutoPorCategoriaResponse(categoriaId, pageable);
        } else {
            paginaResultados = produtoService.listarTodosProdutosResponse(pageable);
        }
        return ResponseEntity.ok(paginaResultados);
    }
	
	
	

}
