package com.dsys.appfood.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dsys.appfood.domain.model.Usuario;
import com.dsys.appfood.repository.UsuarioRepository;

/**
 * Classe responsavel pela autenticação e validações relacionadas a usuário
 * 
 * Responsabilidade ÚNICA: autenticação e validações relacionadas de usuários
 * 
 * 
 * Este Service NÃO sabe nada sobre HTTP, Apenas processa e lança exceções de negócio.
 */
@Service
public class UsuarioService {
	
	private final UsuarioRepository usuarioRepository;
	private final PasswordEncoder passwordEncoder;
	
	public UsuarioService(UsuarioRepository usuarioRepository, 
			PasswordEncoder passwordEncoder) {
		this.usuarioRepository = usuarioRepository;
		this.passwordEncoder = passwordEncoder;
	}
	
	//=============================================================
	// CADASTRO - encode() uma única vez
	//=============================================================
	@Transactional
	public Usuario cadastrar (Usuario usuario, String SenhaDigitada) {
		
		// Verifica se o login já existe e se está Ativo 
		if(usuarioRepository.findByLoginAndAtivoTrue(usuario.getLogin()).isPresent()) {
			throw new IllegalArgumentException("Login já cadastrado.");
		}
		
		//=============================================================
		// ENCODE: transforma a senha em hash antes de salvar.
		// O banco NUNCA verá a senha original
		//=============================================================
		String senhaHash = passwordEncoder.encode(SenhaDigitada);
		usuario.setSenha(senhaHash);
		
		return usuarioRepository.save(usuario);
	}
	
	//=============================================================
	// ATIVAR
	//=============================================================
	@Transactional
	public void ativarUsuario(Integer id) {
				
		//Busca Usuario e lança exceção se não existi 
		Usuario usuario =  usuarioRepository.findById(id).orElseThrow(
				() -> new IllegalArgumentException("Usuario não encontrado id: " + id));
		
		//Confirma se usuario está inativo
		if(usuario.isAtivo()) {
			throw new IllegalArgumentException("Usuario ja ativo id: " + id );
		}
		
		usuario.ativar();
		
		usuarioRepository.save(usuario);
		
	}
	
	//=============================================================
		// ATIVAR
		//=============================================================
		@Transactional
		public void inativarUsuario(Integer id) {
					
			//Busca Usuario e lança exceção se não existir 
			Usuario usuario = usuarioRepository.findById(id).orElseThrow(
					() -> new IllegalArgumentException("Usuario não encontrado id: " + id));
			
			//Confirma se usuario está ativo
			if(!usuario.isAtivo()) {
				throw new IllegalArgumentException("Usuario ja inativo id: " + id );
			}
			
			usuario.inativar();
			
			usuarioRepository.save(usuario);
			
		}
	
	

	//=============================================================
    //  AUTENTICAÇÃO SIMPLES — login + senha
    //  Retorna o Usuario se as credenciais estiverem corretas.
    //  Lança exceção clara se qualquer coisa estiver errada.
    //
    //  IMPORTANTE: propositalmente não dizemos se foi o login
    //  ou a senha que errou — isso é uma boa prática de segurança.
    //  Se dissermos "senha errada", confirmamos que o login existe.
    //=============================================================
	
	@Transactional(readOnly = true)
	public Usuario autenticar(String login, String senhaDigitada) {
		
		Usuario usuario = usuarioRepository.findByLoginAndAtivoTrue(login)
				.orElseThrow(() -> new IllegalArgumentException("Login ou senha inválidos"));
		
		//=============================================================
		// MATCHES: compara  a senha digitada com o hash salvo.
		//
		// NÃO faça: usuario.getSenha().equals(senhaDigitada) <- ERRADO
		// FAÇA :    passwordEncoder.matches(digitada, hash)  <- CERTO
		//
		// matches() extrai o salt do hash e processa a senha com o 
		// mesmo salt - sem precisar guardar o salt separado
		//=============================================================
		if(!passwordEncoder.matches(senhaDigitada, usuario.getSenha())) {
			throw new IllegalArgumentException("Login ou senha inválidos");
		}
		
		return usuario;
	}
	
	//=============================================================
	// TROCAR SENHA - encode novamente
	//=============================================================
	@Transactional
	public void trocarSenha (Integer usuarioId, String senhaAtual, String novaSenha) {
		
		Usuario usuario = buscaPorId(usuarioId);
		
		//Confirma que conhece a senha atual antes de trocar
		if(!passwordEncoder.matches(senhaAtual, usuario.getSenha())) {
			throw new IllegalArgumentException("Senha atual incoreta");
		}
		
		// Valida a nova senha (regras do negócio)
		if(novaSenha.length() < 8 ) {
			throw new IllegalArgumentException("Senha deve ter no mínimo 8 caracteres.");
		}
		
		// Codifica e salva - mesmo processo de cadastro
		usuario.setSenha(passwordEncoder.encode(novaSenha));
		usuarioRepository.save(usuario);
	}
	
	//=============================================================
	// AUTENTICAR GERENTE - Reutiliza autenticar()
	//=============================================================
	
	@Transactional(readOnly = true)
	public Usuario autenticarGerente(String login, String senha) {
		Usuario usuario = autenticar(login, senha); // Já usa o BCrypt
		
		if(!usuario.isGerente()) {
			throw new IllegalArgumentException("Acesso negado. Apenas Gerente");
		}
		
		return usuario;
	}
	
	//=============================================================
	// BUSCA SIMPLES POR ID - reutilizado por outros Services
	// Centraliza a mensagem de erro em um só lugar.
	// Se sepois mudar a mensagem, muda só aqui
	//=============================================================
	@Transactional (readOnly = true)
	public Usuario buscaPorId(Integer id) {
		return usuarioRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException(
						"Usuário não encontrado: id " + id));
	}
	

}
