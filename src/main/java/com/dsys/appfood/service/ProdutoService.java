package com.dsys.appfood.service;

import java.math.BigDecimal;
import java.util.List;import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dsys.appfood.domain.model.Categoria;
import com.dsys.appfood.domain.model.PrecoVariavel;
import com.dsys.appfood.domain.model.Produto;
import com.dsys.appfood.domain.model.Tamanho;
import com.dsys.appfood.dto.PrecoTamanhoRequest;
import com.dsys.appfood.repository.ProdutoRepository;
import com.dsys.appfood.repository.TamanhoRepository;

/**
 * Classe responsavel por gerenciar os Produtos do cardápio.
 * 
 * Responsabilidade ÚNICA: regras de negócio relacionadas ao produto
 * 
 * Este Service NÃO sabe nada sobre HTTP, 
 * Apenas processa e lança exceções de negócio.
 */
@Service
public class ProdutoService {
	
	private final ProdutoRepository produtoRepository;
	private final CategoriaService categoriaService;
	private final TamanhoService tamanhoService;
	
	
	public ProdutoService (ProdutoRepository produtoRepository, CategoriaService categoriaService, TamanhoService tamanhoService) {
		this.produtoRepository = produtoRepository;
		this.categoriaService = categoriaService;
		this.tamanhoService = tamanhoService;
		
		
	}
	
	//=============================================================
	// CADASTRO
	//=============================================================
		
		@Transactional
		public Produto cadastrarProduto(String nome, boolean imprimeCozinha, Integer categoriaId, List<PrecoTamanhoRequest> precos) {
			
			//VALIDAÇÕES BARATAS 
			//1. nome nulo ou vazio
			if(nome == null || nome.isBlank()) {
				throw new IllegalArgumentException("O nome do produto deve ser informado");
			}
			
			//2. lista de preços nula ou vazia
			if(precos == null || precos.isEmpty()) {
				throw new IllegalArgumentException("Os preços do produto devem ser informado");
			}
			
			//3. algum valor negativo na lista			
			if(precos.stream().anyMatch(p ->p.getValor().compareTo(BigDecimal.ZERO) < 0 )) {
				throw new IllegalArgumentException("O preço do produto não pode ser negativo.");
			}
			
			
			// VALIDAÇÕES COM BANCO
			
			//4. buscar categoria pelo id
			Categoria categoria = categoriaService.buscarPorId(categoriaId);
			
			//5. verificar se já existe produto com esse nome nessa categoria
			
			boolean temProDutoNaCategoria = produtoRepository.existsByNomeIgnoreCaseAndCategoria(nome, categoria);
			
			if(temProDutoNaCategoria) {
				throw new IllegalArgumentException(
						"Já existe o produto: " + nome + " na categoria " + categoria.getNome());
			}
			
			 // MONTAR E SALVAR
			
			//6. criar objeto 
			Produto produto = new Produto(nome, categoria, imprimeCozinha);
			
			// 7. Para cada preço recebido, criar PrecoVariavel e adicionar no Produto
			for(PrecoTamanhoRequest preco : precos) {
				Tamanho tamanho = tamanhoService.buscarPorId(preco.getTamanhoId());
				PrecoVariavel pv = new PrecoVariavel(produto, tamanho, preco.getValor());
				produto.adicionarPreco(pv);
			}
			
		
			// RETORNAR
			return produtoRepository.save(produto);
			
		}
		
		

}
