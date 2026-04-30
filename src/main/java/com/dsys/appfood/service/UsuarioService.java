package com.dsys.appfood.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dsys.appfood.domain.enums.TipoUsuario;
import com.dsys.appfood.domain.model.Usuario;
import com.dsys.appfood.exception.NegocioException;
import com.dsys.appfood.exception.UsuarioNaoEncontradoException;
import com.dsys.appfood.repository.UsuarioRepository;

/**
 * Classe responsavel pela autenticação e validações relacionadas a usuário
 * 
 * Responsabilidade ÚNICA: autenticação e validações relacionadas de usuários
 * 
 * 
 * Este Service NÃO sabe nada sobre HTTP, Apenas processa e lança exceções de
 * negócio.
 */
@Service
public class UsuarioService {

	private final UsuarioRepository usuarioRepository;
	private final PasswordEncoder passwordEncoder;

	public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
		this.usuarioRepository = usuarioRepository;
		this.passwordEncoder = passwordEncoder;
	}

	// =============================================================
	// CADASTRO - encode() uma única vez
	// =============================================================
	@Transactional
	public Usuario cadastrar(String nome, String login, String senhaDigitada, String telefone, TipoUsuario tipo) {

		// VALIDAÇÕES SEM BANCO
		if (nome == null || nome.isBlank()) {
			throw new IllegalArgumentException("O nome é obrigatório");
		}

		if (login == null || login.isBlank()) {
			throw new IllegalArgumentException("O login é obrigatório");
		}

		if (senhaDigitada == null || senhaDigitada.isBlank()) {
			throw new IllegalArgumentException("A Senha é obrigatória");
		}

		if (senhaDigitada.length() <= 5) {
			throw new IllegalArgumentException("A senha deve ter no mínimo 6 caracteres");
		}

		if (tipo == null) {
			throw new IllegalArgumentException("O tipo é obrigatório");
		}

		// Padroniza Campos de texto
		String nomePadronizado = nome.trim();
		String loginPadronizado = login.trim();
		String senhaPadronizada = senhaDigitada.trim();
		String telefonePadronizado = (telefone != null) ? telefone.trim() : null;

		// Verifica se o login já existe e se está Ativo
		if (usuarioRepository.findByLoginAndAtivoTrue(login).isPresent()) {
			throw new IllegalArgumentException("Login já utilizado.");
		}

		Usuario novoUsuario = new Usuario();
		novoUsuario.setNome(nomePadronizado);
		novoUsuario.setLogin(loginPadronizado);
		novoUsuario.setTelefone(telefonePadronizado);
		novoUsuario.setTipo(tipo);

		// =============================================================
		// ENCODE: transforma a senha em hash antes de salvar.
		// O banco NUNCA verá a senha original
		// =============================================================
		String senhaHash = passwordEncoder.encode(senhaPadronizada);
		novoUsuario.setSenha(senhaHash);

		return usuarioRepository.save(novoUsuario);
	}

	// =============================================================
	// EDITAR
	// =============================================================
	@Transactional
	public Usuario editar(Integer usuarioId, String novoNome, String novoTelefone, TipoUsuario novoTipo) {

		// VALIDAÇÕES SEM BANCO
		if (novoNome == null || novoNome.isBlank()) {
			throw new IllegalArgumentException("O nome deve ser informado");
		}

		if (novoTelefone == null || novoTelefone.isBlank()) {
			throw new IllegalArgumentException("O Telefone deve ser informado");
		}

		if (novoTipo == null) {
			throw new IllegalArgumentException("O tipo de usuario deve ser informado");
		}

		// Padroniza os campos
		String nomePadronizado = novoNome.trim();
		String telefonePadronizado = novoTelefone.trim();

		Usuario usuarioAtualizado = buscaPorId(usuarioId);

		usuarioAtualizado.setNome(nomePadronizado);
		usuarioAtualizado.setTelefone(telefonePadronizado);
		usuarioAtualizado.setTipo(novoTipo);

		return usuarioRepository.save(usuarioAtualizado);
	}
	
	// =============================================================
	// ALTERAR STATUS
	// =============================================================
	@Transactional
	public void alterarStatus(Integer id, Boolean novoStatus) {
		Usuario usuarioStatus = usuarioRepository.findById(id)
				.orElseThrow(() -> new UsuarioNaoEncontradoException(id));
		
		// Só Altera se for Realmente diferente do estado atual 
		if(usuarioStatus.isAtivo().equals(novoStatus)) {
			usuarioStatus.setAtivo(novoStatus);
		}
		
	}


	// =============================================================
	// AUTENTICAÇÃO SIMPLES — login + senha
	// Retorna o Usuario se as credenciais estiverem corretas.
	// Lança exceção clara se qualquer coisa estiver errada.
	//
	// IMPORTANTE: propositalmente não dizemos se foi o login
	// ou a senha que errou — isso é uma boa prática de segurança.
	// Se dissermos "senha errada", confirmamos que o login existe.
	// =============================================================

	@Transactional(readOnly = true)
	public Usuario autenticar(String login, String senhaDigitada) {

		Usuario usuario = usuarioRepository.findByLoginAndAtivoTrue(login)
				.orElseThrow(() -> new IllegalArgumentException("Login ou senha inválidos"));

		// =============================================================
		// MATCHES: compara a senha digitada com o hash salvo.
		//
		// NÃO faça: usuario.getSenha().equals(senhaDigitada) <- ERRADO
		// FAÇA : passwordEncoder.matches(digitada, hash) <- CERTO
		//
		// matches() extrai o salt do hash e processa a senha com o
		// mesmo salt - sem precisar guardar o salt separado
		// =============================================================
		if (!passwordEncoder.matches(senhaDigitada, usuario.getSenha())) {
			throw new IllegalArgumentException("Login ou senha inválidos");
		}

		return usuario;
	}

	// =============================================================
	// TROCAR SENHA - encode novamente
	// =============================================================
	@Transactional
	public Usuario trocarSenha(Integer usuarioId, String senhaAtual, String novaSenha) {

		Usuario usuario = buscaPorId(usuarioId);

		// Confirma que conhece a senha atual antes de trocar
		if (!passwordEncoder.matches(senhaAtual, usuario.getSenha())) {
			throw new IllegalArgumentException("Senha atual inválida");
		}

		// Valida a nova senha (regras do negócio)
		if (novaSenha.length() <= 5) {
			throw new IllegalArgumentException("Senha deve ter no mínimo 6 caracteres.");
		}

		// Codifica e salva - mesmo processo de cadastro
		usuario.setSenha(passwordEncoder.encode(novaSenha));
		return usuarioRepository.save(usuario);
	}

	// =============================================================
	// AUTENTICAR GERENTE - Reutiliza autenticar()
	// =============================================================

	@Transactional(readOnly = true)
	public Usuario autenticarGerente(String login, String senha) {
		Usuario usuario = autenticar(login, senha); // Já usa o BCrypt

		if (!usuario.isGerente()) {
			throw new NegocioException("Acesso negado. Apenas Gerente");
		}

		return usuario;
	}

	// =============================================================
	// BUSCAS
	// =============================================================
	@Transactional(readOnly = true)
	public Usuario buscaPorId(Integer id) {
		return usuarioRepository.findById(id).orElseThrow(() -> new UsuarioNaoEncontradoException(id));
	}
	
	@Transactional(readOnly = true)
	public List<Usuario> buscaAtivosPorNome(String nome){
		if (nome == null || nome.isBlank()) {
			throw new IllegalArgumentException("O Nome deve ser informado");
		}
		
		return usuarioRepository.findByNomeContainingIgnoreCaseAndAtivoTrue(nome);
	}
	
	@Transactional(readOnly = true)
	public List<Usuario> listarUsuariosAtivos(){
		return usuarioRepository.findByAtivoTrue();
	}
	
	@Transactional(readOnly = true)
	public List<Usuario> listarUsuariosInativos(){
		return usuarioRepository.findByAtivoFalse();
	}
	
	@Transactional(readOnly = true)
	public List<Usuario> listarTodosUsuarios(){
		return usuarioRepository.findAll();
	}



}
