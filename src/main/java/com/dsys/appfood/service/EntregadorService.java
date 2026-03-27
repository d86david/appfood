package com.dsys.appfood.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dsys.appfood.domain.model.Entregador;
import com.dsys.appfood.repository.EntregadorRepository;

/**
 * Classe responsavel por gerenciar os entregadores.
 * 
 * Responsabilidade ÚNICA: regras de negócio relacionadas ao entregador
 * 
 * Este Service NÃO sabe nada sobre HTTP, Apenas processa e lança exceções de
 * negócio.
 */
@Service
public class EntregadorService {

	private final EntregadorRepository entregadorRepository;

	public EntregadorService(EntregadorRepository entregadorRepository) {

		this.entregadorRepository = entregadorRepository;

	}

	// =============================================================
	// CADASTRO
	// =============================================================

	@Transactional
	public Entregador cadastrarEntregdor(String nome, String telefone, BigDecimal valor, BigDecimal diaria) {

		// Validações sem Banco

		if (nome == null || nome.isBlank()) {
			throw new IllegalArgumentException("O nome deve ser informado");
		}

		if (telefone == null || telefone.isBlank()) {
			throw new IllegalArgumentException("O telefone deve ser informado");
		}

		// Padroniza o nome do entregador
		String nomePadronizado = nome.trim();

		// Validações com banco
		if (entregadorRepository.existsByTelefoneAndAtivoTrue(telefone)) {
			throw new IllegalStateException("Já existe um entregador ativo com o telefone: " + telefone);
		}

		// MONTAR E SALVAR
		Entregador entregador = new Entregador(nomePadronizado, telefone);
		entregador.definirValorPorEntrega(valor);
		entregador.definirValorDiaria(diaria);
		return entregadorRepository.save(entregador);

	}

	// =============================================================
	// EDIÇÃO
	// =============================================================
	@Transactional
	public Entregador editarEntregador(Integer id, String novoNome, String novoTelefone, BigDecimal novoValor, BigDecimal novaDiaria) {

		// VALIDAÇÕES SEM BANCO
		if (novoNome == null || novoNome.isBlank()) {
			throw new IllegalArgumentException("O nome deve ser informado");
		}

		if (novoTelefone == null || novoTelefone.isBlank()) {
			throw new IllegalArgumentException("O telefone deve ser informado");
		}

		// Padroniza o nome do entregador
		String nomePadronizado = novoNome.trim();
		
		//VALIDAÇÕES COM BANCO 
		
		//Busca o entregadoe no banco 
		Entregador entregador = entregadorRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Entregador não encontrado id: " + id));
		
		// Verifica se outro registro tem o mesmo telefone
		entregadorRepository.findByTelefoneIgnoreCaseAndAtivoTrue(novoTelefone)
							.ifPresent(existente -> {
								if(!existente.getId().equals(id)) {
									throw new IllegalStateException(
											"Ja existe um entregador ativo com o telefone: " + novoTelefone);
								}
							});
		
		// MONTAR E SALVAR
		entregador.setNome(nomePadronizado);
		entregador.setTelefone(novoTelefone);
		entregador.definirValorPorEntrega(novoValor);
		entregador.definirValorDiaria(novaDiaria);
		
		return entregadorRepository.save(entregador);

	}

	// =============================================================
	// ATIVAÇÃO
	// =============================================================
	
	@Transactional
	public void ativarEntregador(Integer id) {
		
		// BUSCA ENTREGADOR POR ID
		Entregador entregador =  entregadorRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Entregador não encontrado id: " + id));
		
		//USA O OBJETO EM MEÓRIA 
		if(entregador.isAtivo()) {
			throw new IllegalArgumentException("Entregador ja ativo id: " + id );
		}
		
		//ATIVAR
		entregador.ativar();
		
		entregadorRepository.save(entregador);
		
	}

	// =============================================================
	// INATIVAÇÃO
	// =============================================================
	@Transactional
	public void inativarEntregador(Integer id) {
		
		// BUSCA ENTREGADOR POR ID
		Entregador entregador = entregadorRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Entregador não encontrado id: " + id));
		
		//VERIFICA SE O ENTREGADOR JA ESTÁ INATIVO 
		if(!entregador.isAtivo()) {
			throw new IllegalArgumentException("Entregador ja inativo id: " + id );
		}
		
		//INATIVAR
		entregador.inativar();
		
		entregadorRepository.save(entregador);
		
	}
	

	// =============================================================
	// BUSCAS
	// =============================================================

	// Listar todos os entregadores
	@Transactional(readOnly = true)
	public List<Entregador> listarTodosEntregadores() {
		return entregadorRepository.findAll();
	}

	// Busca Entregador por ID
	@Transactional(readOnly = true)
	public Entregador buscarEntregadorPorId(Integer id) {
		return entregadorRepository.findById(id)
				.orElseThrow(() -> new IllegalStateException("Entregador não encontrado id" + id));
	}

	// Busca Entregador pelo nome
	@Transactional(readOnly = true)
	public List<Entregador> buscaEntregadorPorNome(String nome) {
		if (nome == null || nome.isBlank()) {
	        throw new IllegalArgumentException("O nome deve ser informado.");
	    }
		return entregadorRepository.findByNomeContainingIgnoreCase(nome);
	}

}
