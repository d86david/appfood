package com.dsys.appfood.service;

import com.dsys.appfood.domain.model.PrecoVariavel;
import com.dsys.appfood.domain.model.Produto;
import com.dsys.appfood.domain.model.Tamanho;
import com.dsys.appfood.exception.EntidadeNaoEncontradaException;
import com.dsys.appfood.exception.NegocioException;
import com.dsys.appfood.exception.PrecoVariavelNaoEncontradoException;
import com.dsys.appfood.repository.PrecoVariavelRepository;

import com.dsys.appfood.repository.ProdutoRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Serviço para gerenciamento dos preços variáveis dos produtos
 *  
 * 1. Entidade de Associação 
 *  - PrecoVariavel conecta Produto e Tamanho com um valor monetário
 *  - É uma tabela de ligação enriquecida
 *  
 *  2. Este Serviço complementa o ProdutoService
 *   - O ProdutoService ja gerencia a lista de preços ao cadastrar/editar um produto
 *   - Este serviço oferece operações granulares para manutenção avulsa
 *  
 */
@Service
public class PrecoVariavelService {
	
	private final ProdutoRepository produtoRepository;
	private final TamanhoService tamanhoService;
	private final ProdutoService produtoService;
	private final PrecoVariavelRepository precoVariavelRepository;

	public PrecoVariavelService(PrecoVariavelRepository precoVariavelRepository, ProdutoService produtoService, TamanhoService tamanhoService, ProdutoRepository produtoRepository) {
		 this.precoVariavelRepository = precoVariavelRepository;
		 this.produtoService = produtoService;
		 this.tamanhoService = tamanhoService;
		 this.produtoRepository = produtoRepository;
	}
	
	/**
	 * Adiciona ou atualiza um preço para um produto em um tamanho específico 
	 * Se já existir um preço para essa combinação, o valor é atualizado
	 */
	@Transactional
	public PrecoVariavel definirPreco(Integer produtoId, Integer tamanhoId, BigDecimal valor) {
		
		// VALIDAÇÕES SEM BANCO
		if(valor == null || valor.compareTo(BigDecimal.ZERO) < 0) {
			throw new IllegalArgumentException("O preço deve ser maior ou igual a zero");
		}
		
		Produto produto = produtoService.buscarProdutoPorId(produtoId);
		Tamanho tamanho = tamanhoService.buscarPorId(tamanhoId);
		
		// Verifica se já existe um preço para essa combinação
		return precoVariavelRepository.findByProdutoAndTamanho(produto, tamanho)
				.map(existente -> {
					existente.setValor(valor);
					return precoVariavelRepository.save(existente);
				})
				.orElseGet(() ->{
					PrecoVariavel novo = new PrecoVariavel(produto, tamanho, valor);
					produto.adicionarPreco(novo); // Mantém a direção bidirecional.
					return precoVariavelRepository.save(novo);
				});
		
		
	}
	
	/**
     * Remove um preço variável específico.
     * 
     * @throws NegocioException se for o único preço do produto
     */
    @Transactional
    public void removerPreco(Integer precoId) {
        PrecoVariavel preco = precoVariavelRepository.findById(precoId)
                .orElseThrow(() -> new PrecoVariavelNaoEncontradoException(precoId));

        Produto produto = preco.getProduto();
        if (produto.getPrecosVariaveis().size() <= 1) {
            throw new NegocioException("Não é possível remover o único preço do produto.");
        }

        produto.getPrecosVariaveis().remove(preco);
        precoVariavelRepository.delete(preco);
    }

    /**
     * Busca o preço de um produto para um tamanho específico.
     * 
     * @return o valor do preço
     * @throws PrecoVariavelNaoEncontradoException se não existir preço cadastrado
     */
    @Transactional(readOnly = true)
    public BigDecimal obterPreco(Integer produtoId, Integer tamanhoId) {
        Produto produto = produtoService.buscarProdutoPorId(produtoId);
        Tamanho tamanho = tamanhoService.buscarPorId(tamanhoId);

        return precoVariavelRepository.findByProdutoAndTamanho(produto, tamanho)
                .map(PrecoVariavel::getValor)
                .orElseThrow(() -> new EntidadeNaoEncontradaException(
                        String.format("Produto ID %d não possui preço para o tamanho ID %d", produtoId, tamanhoId)));
    }

    /**
     * Lista todos os preços de um produto, com os respectivos tamanhos.
     */
    @Transactional(readOnly = true)
    public List<PrecoVariavel> listarPrecosDoProduto(Integer produtoId) {
        Produto produto = produtoService.buscarProdutoPorId(produtoId);
        return produto.getPrecosVariaveis();
    }

    /**
     * Reajusta todos os preços de um produto aplicando um percentual.
     * 
     * @param produtoId ID do produto
     * @param percentual valor percentual (ex: 10 para aumentar 10%, -5 para reduzir 5%)
     */
    @Transactional
    public void reajustarPrecos(Integer produtoId, BigDecimal percentual) {
        if (percentual == null) {
            throw new IllegalArgumentException("Percentual não pode ser nulo.");
        }

        Produto produto = produtoService.buscarProdutoPorId(produtoId);
        BigDecimal fator = BigDecimal.ONE.add(percentual.divide(new BigDecimal("100")));

        for (PrecoVariavel preco : produto.getPrecosVariaveis()) {
            BigDecimal novoValor = preco.getValor().multiply(fator).setScale(2, RoundingMode.HALF_UP);
            preco.setValor(novoValor);
        }
        produtoRepository.save(produto); // ou produtoRepository.save(produto)
    }

}
