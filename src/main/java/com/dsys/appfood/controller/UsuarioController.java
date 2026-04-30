package com.dsys.appfood.controller;

import com.dsys.appfood.domain.model.Usuario;
import com.dsys.appfood.dto.UuarioLoginRequest;
import com.dsys.appfood.dto.UsuarioAlterarSenhaRequest;
import com.dsys.appfood.dto.UsuarioCadastroRequest;
import com.dsys.appfood.dto.UsuarioEdicaoRequest;
import com.dsys.appfood.dto.UsuarioResponse;
import com.dsys.appfood.dto.UsuarioStatusRequest;
import com.dsys.appfood.service.ProdutoService;
import com.dsys.appfood.service.UsuarioService;

import jakarta.validation.Valid;

import java.net.URI;
import java.util.List;

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
@RequestMapping("/api/usuarios")
public class UsuarioController {
	
	private final UsuarioService usuarioService;

	public UsuarioController(UsuarioService usuarioService, ProdutoService produtoService) {
		this.usuarioService = usuarioService;
	}
	
	// =============================================================
    // 1. CADASTRAR NOVO USUÁRIO
    // =============================================================
	@PostMapping
	public ResponseEntity<UsuarioResponse> cadastrar(
			@RequestBody @Valid UsuarioCadastroRequest request,
			UriComponentsBuilder uriBuilder){
		
		// 1. Chamar o Service para executar as regras de negócio e salvar 
		Usuario usuarioSalvo = usuarioService.cadastrar(request.nome(), request.login(), request.senha(), request.telefone(), request.tipo());
		
		// 2. Retornar o código 201 (Created) e a URL do novo recurso
		URI uri = uriBuilder.path("/usuarios/{id}").buildAndExpand(usuarioSalvo.getId()).toUri();
		
		// 3. Devolver o DTO de Saída (Response)
		return ResponseEntity.created(uri).body(UsuarioResponse.from(usuarioSalvo));
	}
	
	// =============================================================
    // 2. EDITAR USUÁRIO
    // =============================================================
	@PutMapping("/{id}")
	public ResponseEntity<UsuarioResponse> atualizar(
			@PathVariable Integer id,
			@RequestBody @Valid UsuarioEdicaoRequest request){
		
		// 1. Chamar o Service para executar as regras de negócio e atualizar 
		Usuario usuarioAtualizado = usuarioService.editar(id, request.nome(), request.telefone(), request.tipo());
		
		// 2. Devolver o DTO de Saída (Response)
		return ResponseEntity.ok(UsuarioResponse.from(usuarioAtualizado));
	}
	
	// =============================================================
    // 3. ATIVAR/INATIVAR USUÁRIO 
    // =============================================================
	@PatchMapping("/{id}/status")
	public ResponseEntity<Void> atualizarStatus(@PathVariable Integer id,
			@RequestBody @Valid UsuarioStatusRequest request){
		
		usuarioService.alterarStatus(id, request.ativo());
		
		return ResponseEntity.noContent().build();
	}
	
	// =============================================================
    // 4. LOGIN
    // =============================================================
	@PostMapping("/login")
	public ResponseEntity<UsuarioResponse> login(@RequestBody @Valid UuarioLoginRequest request){
		
		// A Service ja fáz a validação com BCrypt e lança exceção se estiver errado
		Usuario usuarioAutenticado = usuarioService.autenticar(request.login(), request.senha());
		
		return ResponseEntity.ok(UsuarioResponse.from(usuarioAutenticado));
	}
	
	// =============================================================
    // 5. ALTERAR SENHA USUÁRIO (PUT)
    // =============================================================
	@PatchMapping("/{id}/senha")
	public ResponseEntity<UsuarioResponse> trocarSenha(
			@PathVariable Integer id,
			@RequestBody @Valid UsuarioAlterarSenhaRequest request){
		// 1. Chamar o Service para executar as regras de negócio e atualizar 
		usuarioService.trocarSenha(id, request.senhaAtual(), request.senhaNova());
		
		// 2. Devolver o DTO de Saída (Response)
		return ResponseEntity.noContent().build(); // Retorna 204
	}
	
	// =============================================================
    // 6. BUSCAR USUÁRIO POR ID
    // =============================================================
	@GetMapping("/{id}")
	public ResponseEntity<UsuarioResponse> buscarPoId(@PathVariable Integer id){
		Usuario usuario = usuarioService.buscaPorId(id);
		return ResponseEntity.ok(UsuarioResponse.from(usuario));
	}
	
	// =============================================================
    // 7. OUTRAS BUSCAS
    // =============================================================
	@GetMapping
	public ResponseEntity<List<UsuarioResponse>> listar(
			@RequestParam(required = false) String nome,
			@RequestParam(required = false, defaultValue = "ATIVO") String status){
		
		
		List<UsuarioResponse> lista;
		
		if(nome != null && !nome.isBlank()) {
			lista = usuarioService.buscaAtivosPorNome(nome)
					.stream()
					.map(UsuarioResponse::from)
					.toList();
		}else if("INATIVOS".equalsIgnoreCase(status)) {
			lista = usuarioService.listarUsuariosInativos()
					.stream()
					.map(UsuarioResponse::from)
					.toList();
		}else if("TODOS".equalsIgnoreCase(status)) {
			lista = usuarioService.listarTodosUsuarios()
					.stream()
					.map(UsuarioResponse::from)
					.toList();
		}else {
			lista = usuarioService.listarUsuariosAtivos()
					.stream()
					.map(UsuarioResponse::from)
					.toList();
		}
		
		return ResponseEntity.ok(lista);
		
	}
	
}
