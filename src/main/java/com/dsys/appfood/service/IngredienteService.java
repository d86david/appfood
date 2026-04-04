package com.dsys.appfood.service;

import com.dsys.appfood.domain.model.Ingrediente;
import com.dsys.appfood.exception.IngredienteJaCadastradoException;
import com.dsys.appfood.exception.IngredienteNaoEncontradoException;
import com.dsys.appfood.exception.NegocioException;
import com.dsys.appfood.repository.ComposicaoPadadraoRepository;
import com.dsys.appfood.repository.IngredienteRepository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Classe responsavel pela autenticação e validações relacionadas ao Ingrediente
 * 
 * Responsabilidade ÚNICA: autenticação e validações relacionadas de endereços
 * 
 * Este Service NÃO sabe nada sobre HTTP, Apenas processa e lança exceções de
 * negócio.
 */
@Service
public class IngredienteService {

	private final ComposicaoPadadraoRepository composicaoPadadraoRepository;
	private final IngredienteRepository ingredienteRepository;

	public IngredienteService(IngredienteRepository ingredienteRepository, ComposicaoPadadraoRepository composicaoPadadraoRepository) {

		this.ingredienteRepository = ingredienteRepository;
		this.composicaoPadadraoRepository = composicaoPadadraoRepository;
	}

	// =============================================================
	// CADASTRO
	// =============================================================
	@Transactional
	public Ingrediente cadastrarIngrediente(String nome, BigDecimal valorAdicional) {

		// VALIDAÇÃO SEM BANCO
		if (nome == null || nome.isBlank()) {
			throw new IllegalArgumentException("O ingrediente deve ser informado");
		}

		if (valorAdicional.signum() <= 0) {
			valorAdicional = BigDecimal.ZERO;
		}

		String nomePadronizado = nome.trim();

		// VERIFICA SE JA TEM O INGREDIENTE
		if (ingredienteRepository.findByNomeIgnoreCase(nomePadronizado).isPresent()) {
			throw new IngredienteJaCadastradoException(nomePadronizado);
		}

		Ingrediente ingrediente = new Ingrediente(nomePadronizado, valorAdicional);

		return ingredienteRepository.save(ingrediente);

	}

	// =============================================================
	// EDIÇÃO
	// ============================================================

	@Transactional
	public Ingrediente editarIngrediente(Integer id, String novoNome, BigDecimal novoValorAdicional) {

		// VALIDAÇÃO SEM BANCO
		if (novoNome == null || novoNome.isBlank()) {
			throw new IllegalArgumentException("O ingrediente deve ser informado");
		}

		if (novoValorAdicional.signum() <= 0) {
			novoValorAdicional = BigDecimal.ZERO;
		}

		String nomePadronizado = novoNome.trim();

		Ingrediente ingrediente = ingredienteRepository.findById(id)
				.orElseThrow(() -> new IngredienteNaoEncontradoException(id));

		ingredienteRepository.findByNomeIgnoreCase(nomePadronizado).ifPresent(existente -> {
			if (!existente.getId().equals(id)) {
				throw new IngredienteJaCadastradoException(nomePadronizado);
			}
		});

		ingrediente.setNome(nomePadronizado);
		ingrediente.atualizarValorAdicional(novoValorAdicional);

		return ingredienteRepository.save(ingrediente);

	}

	// =============================================================
	// EXCLUSÃO
	// =============================================================
	
	@Transactional
	public void excluirIngrediente(Integer id) {
		
		// Verificar se o ingrediente existe 
		if(!ingredienteRepository.existsById(id)) {
			throw new IngredienteNaoEncontradoException(id); 
		}
		
		// VErifica se esse ingrediente faz parte de alguma composição Padrão
		if(composicaoPadadraoRepository.existsByIngredientesId(id)) {
			throw new NegocioException("Esse ingrediente não pode ser excludo!\n"
					+ "Ele é parte de composição de produtos");
		}
		
		ingredienteRepository.deleteById(id);
		
	}

	// =============================================================
	// BUSCAS
	// =============================================================
	@Transactional(readOnly = true)
	public List<Ingrediente> listarTodosIngredientes() {
		return ingredienteRepository.findAll();
	}
	
	@Transactional(readOnly = true)
	public Ingrediente buscarIngredientePorId(Integer id) {
		return ingredienteRepository.findById(id)
				.orElseThrow(() -> new IngredienteNaoEncontradoException(id));
	}

	
}
