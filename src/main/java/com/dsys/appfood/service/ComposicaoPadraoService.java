package com.dsys.appfood.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dsys.appfood.domain.model.ComposicaoPadrao;
import com.dsys.appfood.domain.model.Ingrediente;
import com.dsys.appfood.domain.model.Produto;
import com.dsys.appfood.exception.NegocioException;
import com.dsys.appfood.repository.ComposicaoPadadraoRepository;

/**
 * Classe responsavel pelas composições dos produtos, ela é a receita dos
 * produtos
 * 
 * Responsabilidade ÚNICA: compor os ingredientes padrões nos produtos
 * 
 * 
 * Este Service NÃO sabe nada sobre HTTP, Apenas processa e lança exceções de
 * negócio.
 */
@Service
public class ComposicaoPadraoService {

	private final ComposicaoPadadraoRepository composicaoRepository;
	private final ProdutoService produtoService;
	private final IngredienteService ingredienteService;

	// Injeção de Dependências
	public ComposicaoPadraoService(ComposicaoPadadraoRepository composicaoRepository, ProdutoService produtoService,
			IngredienteService ingredienteService) {
		this.composicaoRepository = composicaoRepository;
		this.produtoService = produtoService;
		this.ingredienteService = ingredienteService;
	}

	// =============================================================
	// 1. CRIAR A RECEITA (COMPOSIÇÃO) PARA UM PRODUTO
	// =============================================================
	@Transactional
	public ComposicaoPadrao definirComposicao(Integer produtoId, List<Integer> ingredientesIds) {
		// Buscar o Produto usando o produtoService

		Produto produto = produtoService.buscarProdutoPorId(produtoId);

		// Verificar se este produto JÁ TEM uma composição salva no banco
		if (composicaoRepository.findByProdutoId(produtoId).isPresent()) {
			throw new NegocioException("Ja existe uma composição para esse produto");
		}

		// Instancia uma nova ComposicaoPadrao e seta o produto nela.
		ComposicaoPadrao composicao = new ComposicaoPadrao();

		composicao.setProduto(produto);

		// Fazer um "for" na lista de ingredientesIds:
		for (Integer ingredienteId : ingredientesIds) {
			Ingrediente ingrediente = ingredienteService.buscarIngredientePorId(ingredienteId);

			// REGRA: só adiciona se não tiver na lista (evita duplicatas na criação)
			if (!composicao.getIngredientes().contains(ingrediente)) {
				
				composicao.adicionarIngrediente(ingrediente);
			}
			
		}

		// Salvar e retornar a composição
		return composicaoRepository.save(composicao);
	}

	// =============================================================
	// 2. ADICIONAR UM INGREDIENTE EXTRA NA RECEITA EXISTENTE
	// =============================================================
	@Transactional
	public ComposicaoPadrao adicionarIngredienteNaComposicao(Integer produtoId, Integer ingredienteId) {
		// Buscar a Composição pelo produtoId (lance exceção se o produto ainda não
		// tiver receita)
		ComposicaoPadrao composicao = composicaoRepository.findByProdutoId(produtoId)
				.orElseThrow(() -> new NegocioException("Não existe um composição com esse produto"));

		// Busca o Ingrediente pelo Id
		Ingrediente ingrediente = ingredienteService.buscarIngredientePorId(ingredienteId);

		// REGRA DE NEGÓCIO: Verifica se o ingrediente JÁ ESTÁ na lista dessa
		// composição.
		if (composicao.getIngredientes().contains(ingrediente)) {
			throw new NegocioException(
					"O ingrediente " + ingrediente.getNome() + " já está na lista de composição desse produto");
		}

		// Adicionar o ingrediente e salva a composição
		composicao.adicionarIngrediente(ingrediente);
		return composicaoRepository.save(composicao);
	}

	// =============================================================
	// 3. REMOVER UM INGREDIENTE DA RECEITA
	// =============================================================
	@Transactional
	public ComposicaoPadrao removerIngredienteDaComposicao(Integer produtoId, Integer ingredienteId) {
		// Busca a Composição pelo produtoId
		ComposicaoPadrao composicao = composicaoRepository.findByProdutoId(produtoId)
				.orElseThrow(() -> new NegocioException("Não existe uma composição com esse produto"));

		// Busca o Ingrediente pelo Id
		Ingrediente ingrediente = ingredienteService.buscarIngredientePorId(ingredienteId);

		// Remove o ingrediente
		composicao.removerIngrediente(ingrediente);

		// Salva a composição
		return composicaoRepository.save(composicao);
	}

	// =============================================================
	// 4. CONSULTA
	// =============================================================
	@Transactional(readOnly = true)
	public ComposicaoPadrao buscarReceitaDoProduto(Integer produtoId) {
		return composicaoRepository.findByProdutoId(produtoId)
				.orElseThrow(() -> new NegocioException("Não existe uma composição para o produto ID: " + produtoId));
	}

}
