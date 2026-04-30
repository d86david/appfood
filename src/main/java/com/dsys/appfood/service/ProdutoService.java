package com.dsys.appfood.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dsys.appfood.domain.model.Categoria;
import com.dsys.appfood.domain.model.PrecoVariavel;
import com.dsys.appfood.domain.model.Produto;
import com.dsys.appfood.domain.model.Tamanho;
import com.dsys.appfood.dto.PrecoTamanhoRequest;
import com.dsys.appfood.dto.ProdutoRequest;
import com.dsys.appfood.dto.ProdutoResponse;
import com.dsys.appfood.exception.ProdutoNaoEncontradoException;
import com.dsys.appfood.repository.ProdutoRepository;


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
			
			String nomePadronizado = nome.trim();
			
			//2. lista de preços nula ou vazia
			if(precos == null || precos.isEmpty()) {
				throw new IllegalArgumentException("Os preços do produto devem ser informado");
			}
			
			//3. algum valor negativo na lista			
			if(precos.stream().anyMatch(p ->p.valor().compareTo(BigDecimal.ZERO) < 0 )) {
				throw new IllegalArgumentException("O preço do produto não pode ser negativo.");
			}
			
			
			// VALIDAÇÕES COM BANCO
			
			//4. buscar categoria pelo id
			Categoria categoria = categoriaService.buscarCategoriaPorId(categoriaId);
			
			//5. verificar se já existe produto com esse nome nessa categoria
			
			boolean temProDutoNaCategoria = produtoRepository.existsByNomeIgnoreCaseAndCategoria(nome, categoria);
			
			if(temProDutoNaCategoria) {
				throw new IllegalStateException(
						"Já existe o produto: " + nome + " na categoria " + categoria.getNome());
			}
			
			 // MONTAR E SALVAR
			
			//6. criar objeto 
			Produto produto = new Produto(nomePadronizado, categoria, imprimeCozinha);
			
			// 7. Para cada preço recebido, criar PrecoVariavel e adicionar no Produto
			for(PrecoTamanhoRequest preco : precos) {
				Tamanho tamanho = tamanhoService.buscarPorId(preco.tamanhoId());
				PrecoVariavel pv = new PrecoVariavel(produto, tamanho, preco.valor());
				produto.adicionarPreco(pv);
			}
			
		
			// RETORNAR
			return produtoRepository.save(produto);
		}
		

		//=============================================================
		// EDIÇÃO
		//=============================================================
		@Transactional
		public Produto editarProduto(Integer id, String novoNome, Integer categoriaId, boolean imprimeCozinha, List<PrecoTamanhoRequest> precos) {
			
			//VALIDAÇÕES SEM BANCO
			//1. nome nulo ou vazio
			if(novoNome == null || novoNome.isBlank()) {
				throw new IllegalArgumentException("O nome do produto deve ser informado");
			}
			
			String nomePadronizado = novoNome.trim();
			
			//2. lista de preços nula ou vazia
			if(precos == null || precos.isEmpty()) {
				throw new IllegalArgumentException("Os preços do produto devem ser informado");
			}
			
			//3. algum valor negativo na lista			
			if(precos.stream().anyMatch(p ->p.valor().compareTo(BigDecimal.ZERO) < 0 )) {
				throw new IllegalArgumentException("O preço do produto não pode ser negativo.");
			}
			
			//4. buscar categoria e produto pelo id
			Categoria categoria = categoriaService.buscarCategoriaPorId(categoriaId);
			 
			Produto produto = produtoRepository.findById(id)
					.orElseThrow(() -> new ProdutoNaoEncontradoException(id));
			
			//5 Verificar se existe um produto com mesmo nome nessa Categoria
			// (ignora o próprio registro que está sendo editado)
			produtoRepository.findByNomeIgnoreCaseAndCategoria(nomePadronizado, categoria)
			.ifPresent(existente -> {
				if(!existente.getId().equals(id)) {
					throw new IllegalStateException(
							"Já existe um produto com o nome: '"+ nomePadronizado
							+ "' na categoria " + categoria.getNome());
				}
			});
			
			//ATUALIZA OS DADOS
			
			//6 Atualiza nome, categoria e imprime cozinha
			produto.setNome(nomePadronizado);
			produto.setCategoria(categoria);
			produto.setImprimeCozinha(imprimeCozinha); 
			
			//7 limpa a lista de preço antigas e adiciona os novos 
			//remove os antigos
			produto.getPrecosVariaveis().clear();
			
			//adiciona os novos
			for(PrecoTamanhoRequest preco : precos) {
				Tamanho tamanho = tamanhoService.buscarPorId(preco.tamanhoId());
				PrecoVariavel pv = new PrecoVariavel(produto, tamanho, preco.valor());
				produto.adicionarPreco(pv);
			}
			
			//SALVAR
			
			//8 Salvar e retornar
			return produtoRepository.save(produto);
			
		}
		
		
		//=============================================================
		// EXCLUSÃO
		//=============================================================
		
		@Transactional
		public void excluirProduto(Integer id) {
			// Confirma que existe antes de tentar excluir
			if(!produtoRepository.existsById(id)) {
				throw new ProdutoNaoEncontradoException(id);
			}
			
			produtoRepository.deleteById(id);
		}
		 
		
		
		//=============================================================
		// CONSULTAS
		//=============================================================
		
		@Transactional(readOnly = true)
		public Produto buscarProdutoPorId(Integer id) {
			return produtoRepository.findById(id)
					.orElseThrow(() -> new ProdutoNaoEncontradoException(id));
		}
		
		@Transactional(readOnly = true)
		public List<Produto> listarTodosProdutos(){
			return produtoRepository.findAll();
		}
		
		
		@Transactional(readOnly = true)
		public List<Produto> buscarProdutoPorNome(String nome){
			
			//Validações
			if(nome == null || nome.isBlank()) {
				throw new IllegalArgumentException("O nome do produto deve ser informado");
			}
			
			return produtoRepository.findByNomeContainingIgnoreCase(nome);
		}
		
		@Transactional(readOnly = true)
		public List<Produto> listarProdutoPorCategoria(Integer categoriaId){
			Categoria categoria = categoriaService.buscarCategoriaPorId(categoriaId);
			
			return produtoRepository.findByCategoria(categoria);
			
		}
		
		@Transactional(readOnly = true)
		public boolean isProdutoNaCategoria(Integer categoriaId) {
			return produtoRepository.existsByCategoriaId(categoriaId);
		}
	
		// =============================================================
		// MÉTODOS DTO (conversão dentro da transação)
		// =============================================================
		@Transactional(readOnly = true)
		public ProdutoResponse buscaProdutoResponsePorId(Integer id) {
			return ProdutoResponse.from(buscarProdutoPorId(id));
		}
		
		@Transactional(readOnly = true)
		public List<ProdutoResponse> listarTodosProdutosResponse() {
		    return listarTodosProdutos().stream()
		            .map(ProdutoResponse::from)
		            .collect(Collectors.toList());
		}
		
		@Transactional(readOnly = true)
		public List<ProdutoResponse> buscarProdutoPorNomeResponse(String nome) {
		    return buscarProdutoPorNome(nome).stream()
		            .map(ProdutoResponse::from)
		            .collect(Collectors.toList());
		}

		@Transactional(readOnly = true)
		public List<ProdutoResponse> listarProdutoPorCategoriaResponse(Integer categoriaId) {
		    return listarProdutoPorCategoria(categoriaId).stream()
		            .map(ProdutoResponse::from)
		            .collect(Collectors.toList());
		}

		@Transactional
		public ProdutoResponse cadastrarProdutoResponse(ProdutoRequest request) {
		    Produto produto = cadastrarProduto(
		        request.nome(),
		        request.imprimeCozinha(),
		        request.categoriaId(),
		        request.precos()
		    );
		    return ProdutoResponse.from(produto);
		}

		@Transactional
		public ProdutoResponse editarProdutoResponse(Integer id, ProdutoRequest request) {
		    Produto produto = editarProduto(
		        id,
		        request.nome(),
		        request.categoriaId(),
		        request.imprimeCozinha(),
		        request.precos()
		    );
		    return ProdutoResponse.from(produto);
		}
		

}
