package com.dsys.appfood.service;

import com.dsys.appfood.domain.model.ComposicaoPadrao;
import com.dsys.appfood.domain.model.Ingrediente;
import com.dsys.appfood.domain.model.Produto;
import com.dsys.appfood.dto.request.IngredienteRequest;
import com.dsys.appfood.dto.response.IngredienteResponse;
import com.dsys.appfood.exception.IngredienteJaCadastradoException;
import com.dsys.appfood.exception.IngredienteNaoEncontradoException;
import com.dsys.appfood.exception.NegocioException;
import com.dsys.appfood.repository.ComposicaoPadadraoRepository;
import com.dsys.appfood.repository.IngredienteRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
	public Page<Ingrediente> listarTodosIngredientes(Pageable pageable) {
		return ingredienteRepository.findAll(pageable);
	}
	
	@Transactional(readOnly = true)
	public Ingrediente buscarIngredientePorId(Integer id) {
		return ingredienteRepository.findById(id)
				.orElseThrow(() -> new IngredienteNaoEncontradoException(id));
	}
	
	@Transactional(readOnly = true)
	public Page<Ingrediente> buscarIngredientePorNome(String nome, Pageable pageable){
		if (nome == null || nome.isBlank()) {
			throw new IllegalArgumentException("O nome deve ser informado");
		}

		return ingredienteRepository.findByNomeContainingIgnoreCase(nome, pageable);
		
	}

	@Transactional(readOnly = true)
	public List<Produto> buscarProdutosPorIngrediente(Integer ingredienteId){
		
		// garante que o ingrediente existe
		buscarIngredientePorId(ingredienteId);
		
		// busca todas as receitas que tem esse ingrediente
		List<ComposicaoPadrao> composicoes = composicaoPadadraoRepository.findByIngredientesId(ingredienteId);
		
		// Extrai apenas o Produto de dentro de cada Composição com stream
		return composicoes.stream()
				.map(ComposicaoPadrao::getProduto)
				.collect(Collectors.toList());
	}
	
	
	// =============================================================
	//  MÉTODOS DTO (conversão dentro da transação)
	// =============================================================
	@Transactional
	public IngredienteResponse cadastrarIngredienteResponse(IngredienteRequest request) {
		
		Ingrediente novoIngrediente = cadastrarIngrediente(request.nome(), request.valorAdicional());
		
		return IngredienteResponse.from(novoIngrediente);
	}
	
	@Transactional
	public IngredienteResponse editarIngredienteResponse (Integer id, IngredienteRequest request) {
		
		Ingrediente ingredienteAtualizado = editarIngrediente(id, request.nome(), request.valorAdicional());
		
		return IngredienteResponse.from(ingredienteAtualizado);
	}
	
	@Transactional(readOnly = true)
	public IngredienteResponse buscarPorIdResponse(Integer id) {
		
		return IngredienteResponse.from(buscarIngredientePorId(id));
	}
	
	@Transactional(readOnly = true)
	public Page<IngredienteResponse> buscarPorNomeResponse(String nome, Pageable pageable){
		
		return buscarIngredientePorNome(nome, pageable).map(IngredienteResponse::from);
	}
	
	@Transactional(readOnly = true)
	public Page<IngredienteResponse> listarTodosIngredientesResponse(Pageable pageable){
		
		return listarTodosIngredientes(pageable).map(IngredienteResponse::from);
	}
}
