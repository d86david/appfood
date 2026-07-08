package com.dsys.appfood.service;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dsys.appfood.domain.model.Borda;
import com.dsys.appfood.dto.request.BordaRequest;
import com.dsys.appfood.dto.response.BordaResponse;
import com.dsys.appfood.exception.BordaJaCadastradaException;
import com.dsys.appfood.exception.BordaNaoEncontradaException;
import com.dsys.appfood.exception.IngredienteJaCadastradoException;
import com.dsys.appfood.repository.BordaRepository;

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
	public Page<Borda> listarTodasBordas(Pageable pageable){
		return bordaRepository.findAll(pageable);
	}

	@Transactional(readOnly = true)
	public Borda buscarBordaPorId(Integer id){
		return bordaRepository.findById(id)
				.orElseThrow(() -> new BordaNaoEncontradaException(id));
	}

	@Transactional(readOnly = true)
	public Page<Borda> buscarBordaPorNome (String nome, Pageable pageable) {
		if (nome == null || nome.isBlank()) {
			throw new IllegalArgumentException("O Nome deve ser informado");
		}

		return bordaRepository.findByNomeContainingIgnoreCase(nome, pageable);
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
	public Page<BordaResponse> buscarPorNomeResponse(String nome, Pageable pageable) {
		return buscarBordaPorNome(nome, pageable)
				.map(BordaResponse::from);

	}

	@Transactional(readOnly = true)
	public Page<BordaResponse> listarTodasAsBordasResponse(Pageable pageable){
		return listarTodasBordas(pageable)
				.map(BordaResponse::from);
	}

}
