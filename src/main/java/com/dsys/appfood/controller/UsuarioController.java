package com.dsys.appfood.controller;

import com.dsys.appfood.domain.model.Usuario;
import com.dsys.appfood.dto.UuarioLoginRequest;
import com.dsys.appfood.dto.UsuarioAlterarSenhaRequest;
import com.dsys.appfood.dto.UsuarioCadastroRequest;
import com.dsys.appfood.dto.UsuarioEdicaoRequest;
import com.dsys.appfood.dto.UsuarioResponse;
import com.dsys.appfood.service.UsuarioService;

import jakarta.validation.Valid;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {
	
	private final UsuarioService usuarioService;

	public UsuarioController(UsuarioService usuarioService) {
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
    // 2. BUSCAR USUÁRIO POR ID (GET)
    // =============================================================
	@GetMapping("/{usuarioId}")
	public ResponseEntity<UsuarioResponse> buscarPoId(@PathVariable Integer usuarioId){
		Usuario usuario = usuarioService.buscaPorId(usuarioId);
		return ResponseEntity.ok(UsuarioResponse.from(usuario));
	}
	
	// =============================================================
    // 3. LOGIN (POST)
    // =============================================================
	@PostMapping("/login")
	public ResponseEntity<UsuarioResponse> login(@RequestBody @Valid UuarioLoginRequest request){
		
		// A Service ja fáz a validação com BCrypt e lança exceção se estiver errado
		Usuario usuarioAutenticado = usuarioService.autenticar(request.login(), request.senha());
		
		return ResponseEntity.ok(UsuarioResponse.from(usuarioAutenticado));
	}
	
	// =============================================================
    // 4. EDITAR USUÁRIO (PUT)
    // =============================================================
	@PutMapping("/{usuarioId}/atualizar")
	public ResponseEntity<UsuarioResponse> atualizar(
			@PathVariable Integer usuarioId,
			@RequestBody @Valid UsuarioEdicaoRequest request){
		
		// 1. Chamar o Service para executar as regras de negócio e atualizar 
		Usuario usuarioAtualizado = usuarioService.editar(usuarioId, request.nome(), request.telefone(), request.tipo());
		
		// 2. Devolver o DTO de Saída (Response)
		return ResponseEntity.ok(UsuarioResponse.from(usuarioAtualizado));
	}
	
	// =============================================================
    // 5. ALTERAR SENHA USUÁRIO (PUT)
    // =============================================================
	@PutMapping("/{usuarioId}/senha")
	public ResponseEntity<UsuarioResponse> trocarSenha(
			@PathVariable Integer usuarioId,
			@RequestBody @Valid UsuarioAlterarSenhaRequest request){
		// 1. Chamar o Service para executar as regras de negócio e atualizar 
		Usuario usuarioSenhaNova = usuarioService.trocarSenha(usuarioId, request.senhaAtual(), request.senhaNova());
		
		// 2. Devolver o DTO de Saída (Response)
		return ResponseEntity.ok(UsuarioResponse.from(usuarioSenhaNova));
	}
	
	// =============================================================
    // 6. ATIVAR USUÁRIO 
    // =============================================================
	@PutMapping("/{usuarioId}/ativar")
	public ResponseEntity<UsuarioResponse> ativar(@PathVariable Integer usuarioId){
		
		Usuario usurioAtivo = usuarioService.ativarUsuario(usuarioId);
		
		return ResponseEntity.ok(UsuarioResponse.from(usurioAtivo));
	}
	
	// =============================================================
    // 6. INATIVAR USUÁRIO 
    // =============================================================
	@PutMapping("/{usuarioId}/inativar")
	public ResponseEntity<UsuarioResponse> inativar(@PathVariable Integer usuarioId){
		
		Usuario usurioAtivo = usuarioService.inativarUsuario(usuarioId);
		
		return ResponseEntity.ok(UsuarioResponse.from(usurioAtivo));
	}
	
}
