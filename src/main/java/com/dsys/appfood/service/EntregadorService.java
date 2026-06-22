package com.dsys.appfood.service;

import java.math.BigDecimal;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dsys.appfood.domain.model.Entregador;
import com.dsys.appfood.dto.request.EntregadorRequest;
import com.dsys.appfood.dto.request.EntregadorStatusRequest;
import com.dsys.appfood.dto.response.EntregadorResponse;
import com.dsys.appfood.exception.EntregadorNaoEncontradoException;
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
				.orElseThrow(() -> new EntregadorNaoEncontradoException(id));
		
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
	// ALTERAR STATUS DO ENTREGADOR 
	// =============================================================
	
	@Transactional
	public void alterarStatusEntregador(Integer id, Boolean novoStatus) {
		
		// BUSCA ENTREGADOR POR ID
		Entregador entregadorStatus =  entregadorRepository.findById(id)
				.orElseThrow(() -> new EntregadorNaoEncontradoException(id));
		
		// Objects.equals previne NullPointerException se novoStatus ou entregadorStatus.isAtivo() forem null
	    // Só entra no bloco se o status for DIFERENTE do atual  
		if(!Objects.equals(entregadorStatus.isAtivo(), novoStatus) ) {
			entregadorStatus.setAtivo(novoStatus);
		}
		
		entregadorRepository.save(entregadorStatus);
		
	}
	

	// =============================================================
	// BUSCAS
	// =============================================================

	// Listar todos os entregadores
	@Transactional(readOnly = true)
	public Page<Entregador> listarTodosEntregadores(Pageable pageable) {
		return entregadorRepository.findAll(pageable);
	}

	// Busca Entregador por ID
	@Transactional(readOnly = true)
	public Entregador buscarEntregadorPorId(Integer id) {
		return entregadorRepository.findById(id)
				.orElseThrow(() -> new EntregadorNaoEncontradoException(id));
	}

	// Busca Entregador pelo nome
	@Transactional(readOnly = true)
	public Page<Entregador> buscaEntregadorPorNome(String nome, Pageable pageable) {
		if (nome == null || nome.isBlank()) {
	        throw new IllegalArgumentException("O nome deve ser informado.");
	    }
		return entregadorRepository.findByNomeContainingIgnoreCase(nome, pageable);
	}
	
	// =============================================================
	//  MÉTODOS DTO (conversão dentro da transação)
	// =============================================================
	
	@Transactional
	public EntregadorResponse cadastrarEntregdorResponse(EntregadorRequest request) {
		
		Entregador entregador = cadastrarEntregdor(request.nome(), request.telefone(), 
				request.valorPorEntrega(), request.valorDiaria());
		
		return EntregadorResponse.from(entregador);
		
	}
	
	@Transactional
	public EntregadorResponse editarEntregadorResponse(Integer id, EntregadorRequest request) {
		
		Entregador entregadorAtualizado = editarEntregador(id, request.nome(), request.telefone(), request.valorPorEntrega(), request.valorDiaria());
		
		return EntregadorResponse.from(entregadorAtualizado);
		
	}
	
	@Transactional
	public void alterarStatusEntregadorResponse(Integer id, EntregadorStatusRequest request) {
		
		alterarStatusEntregador(id, request.ativo());
		
	}
	
	@Transactional(readOnly = true)
	public EntregadorResponse buscarEntregadorPorIdResponse(Integer id) {
		return EntregadorResponse.from(buscarEntregadorPorId(id));
	}
	
	@Transactional(readOnly = true)
	public Page<EntregadorResponse> listarTodosEntregadoresResponse(Pageable pageable){
		return listarTodosEntregadores(pageable).map(EntregadorResponse:: from);
	}
	
	@Transactional(readOnly = true)
	public Page<EntregadorResponse> buscaEntregadorPorNomeResponse(String nome, Pageable pageable){
		
		return buscaEntregadorPorNome(nome, pageable).map(EntregadorResponse::from);
		
	}

}
