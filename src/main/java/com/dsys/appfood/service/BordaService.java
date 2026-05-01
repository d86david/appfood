package com.dsys.appfood.service;

import com.dsys.appfood.domain.model.Borda;
import com.dsys.appfood.dto.BordaRequest;
import com.dsys.appfood.dto.BordaResponse;
import com.dsys.appfood.exception.BordaJaCadastradaException;
import com.dsys.appfood.exception.BordaNaoEncontradaException;
import com.dsys.appfood.exception.IngredienteJaCadastradoException;
import com.dsys.appfood.repository.BordaRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Classe responsavel pela autenticação e validações relacionadas a Borda
 * 
 * Responsabilidade ÚNICA: autenticação e validações relacionadas a bordas
 * 
 * Este Service NÃO sabe nada sobre HTTP, Apenas processa e lança exceções de
 * negócio.
 */
@Service
public class BordaService {

	private final BordaRepository bordaRepository;

	public BordaService(BordaRepository bordaRepository) {

		this.bordaRepository = bordaRepository;

	}

	// =============================================================
	// CADASTRO
	// =============================================================
	@Transactional
	public Borda cadastrarBorda(String nome, BigDecimal valorAdicional) {

		// VALIDAÇÃO SEM BANCO
		if (nome == null || nome.isBlank()) {
			throw new IllegalArgumentException("O ingrediente deve ser informado");
		}

		if (valorAdicional.signum() <= 0) {
			valorAdicional = BigDecimal.ZERO;
		}

		String nomePadronizado = nome.trim();
		
		// Verifica se a Borda ja existe no banco
		if(bordaRepository.findByNomeIgnoreCase(nomePadronizado).isPresent()) {
			throw new BordaJaCadastradaException(nomePadronizado);
		}

		// VERIFICA SE JA TEM O INGREDIENTE
		if (bordaRepository.findByNomeIgnoreCase(nomePadronizado).isPresent()) {
			throw new IngredienteJaCadastradoException(nomePadronizado);
		}

		Borda borda = new Borda(nomePadronizado, valorAdicional);

		return bordaRepository.save(borda);

	}

	// =============================================================
	// EDIÇÃO
	// ============================================================

	@Transactional
	public Borda editarBorda(Integer id, String novoNome, BigDecimal novoValorAdicional) {

		// VALIDAÇÃO SEM BANCO
		if (novoNome == null || novoNome.isBlank()) {
			throw new IllegalArgumentException("O ingrediente deve ser informado");
		}

		if (novoValorAdicional.signum() <= 0) {
			novoValorAdicional = BigDecimal.ZERO;
		}

		String nomePadronizado = novoNome.trim();

		Borda borda = bordaRepository.findById(id).orElseThrow(() -> new BordaNaoEncontradaException(id));

		// Verifica se outro registro ja usa esse nome
		bordaRepository.findByNomeIgnoreCase(nomePadronizado).ifPresent(existente -> {
			if (!existente.getId().equals(id)) {
				throw new BordaJaCadastradaException(nomePadronizado, id);
			}
		});

		borda.setNome(nomePadronizado);
		borda.atualizarValorAdicional(novoValorAdicional);

		return bordaRepository.save(borda);

	}

	// =============================================================
	// EXCLUSÃO
	// =============================================================

	@Transactional
	public void excluirBorda(Integer id) {

		// Verificar se o ingrediente existe
		if (!bordaRepository.existsById(id)) {
			throw new BordaNaoEncontradaException(id);
		}

		bordaRepository.deleteById(id);

	}

	// =============================================================
	// BUSCAS
	// =============================================================
	@Transactional(readOnly = true)
	public List<Borda> listarTodasBordas(){
		return bordaRepository.findAll();
	}
	
	@Transactional(readOnly = true)
	public Borda buscarBordaPorId(Integer id){
		return bordaRepository.findById(id)
				.orElseThrow(() -> new BordaNaoEncontradaException(id));
	}
	
	@Transactional(readOnly = true)
	public List<Borda> buscarBordaPorNome (String nome) {
		if (nome == null || nome.isBlank()) {
			throw new IllegalArgumentException("O Nome deve ser informado");
		}

		return bordaRepository.findByNomeContainingIgnoreCase(nome);
	}
	
	// =============================================================
	//  MÉTODOS DTO (conversão dentro da transação)
	// =============================================================
	@Transactional
	public BordaResponse cadastrarBordaResponse(BordaRequest request) {
		Borda borda = cadastrarBorda(request.nome(), request.valorAdicional());
		
		return BordaResponse.from(borda);
	}
	
	@Transactional
	public BordaResponse editarBordaResponse( Integer id, BordaRequest request) {
		
		Borda novaBorda = editarBorda(id, request.nome(), request.valorAdicional());
		
		return BordaResponse.from(novaBorda);
		
	}
	
	@Transactional(readOnly = true)
	public BordaResponse buscarPorIdResponse(Integer id) {
		return BordaResponse.from(buscarBordaPorId(id));
		
	}
	
	@Transactional(readOnly = true)
	public List<BordaResponse> buscarPorNomeResponse(String nome) {
		return buscarBordaPorNome(nome)
				.stream()
				.map(BordaResponse::from)
				.collect(Collectors.toList());
		
	}
	
	@Transactional(readOnly = true)
	public List<BordaResponse> listarTodasAsBordasResponse(){
		return listarTodasBordas()
				.stream()
				.map(BordaResponse::from)
				.collect(Collectors.toList());
	}
	
}
