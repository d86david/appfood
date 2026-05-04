package com.dsys.appfood.controller;

import com.dsys.appfood.dto.ProdutoRequest;
import com.dsys.appfood.dto.ProdutoResponse;
import com.dsys.appfood.service.ProdutoService;

import jakarta.validation.Valid;

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

@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {
	
	private final ProdutoService produtoService;

	public ProdutoController(ProdutoService produtoService) {
		
		this.produtoService = produtoService;
		
	}
	
	// =============================================================
    // 1. CADASTRAR NOVO PRODUTO
    // =============================================================
	@PostMapping
	public ResponseEntity<ProdutoResponse> cadastrar(@RequestBody @Valid ProdutoRequest request, UriComponentsBuilder uriBuilder){
		
		ProdutoResponse response = produtoService.cadastrarProdutoResponse(request);
		
		URI uri = uriBuilder.path("/produtos/{id}").buildAndExpand(response.id()).toUri();
		
		return ResponseEntity.created(uri).body(response);
		
	}
	
	// =============================================================
    // 2. EDITAR PRODUTO
    // =============================================================
	@PutMapping("/{id}")
	public ResponseEntity<ProdutoResponse> atualizar(@PathVariable Integer id, @RequestBody @Valid ProdutoRequest request){
		
		ProdutoResponse response = produtoService.editarProdutoResponse(id, request);
		return ResponseEntity.ok(response);
	}
	
	
	// =============================================================
    // 3. EXCLUIR PRODUTO
    // =============================================================
	@DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Integer id) {
        produtoService.excluirProduto(id);
        return ResponseEntity.noContent().build();
    }
	
	
	// =============================================================
    // 6. BUSCAR PRODUTO POR ID
    // =============================================================
	@GetMapping("/{id}")
	public ResponseEntity<ProdutoResponse> buscarPorId(@PathVariable Integer id){
		ProdutoResponse response = produtoService.buscaProdutoResponsePorId(id);
		
		return ResponseEntity.ok(response);
		
	}
	
	// =============================================================
    // 7. OUTRAS BUSCAS
    // =============================================================
	@GetMapping
    public ResponseEntity<List<ProdutoResponse>> listar(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) Integer categoriaId) {

        List<ProdutoResponse> lista;
        if (nome != null) {
            lista = produtoService.buscarProdutoPorNomeResponse(nome);
        } else if (categoriaId != null) {
            lista = produtoService.listarProdutoPorCategoriaResponse(categoriaId);
        } else {
            lista = produtoService.listarTodosProdutosResponse();
        }
        return ResponseEntity.ok(lista);
    }

}
