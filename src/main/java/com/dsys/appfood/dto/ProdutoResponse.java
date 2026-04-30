package com.dsys.appfood.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.dsys.appfood.domain.model.Produto;

public record ProdutoResponse(
		
		Integer id,
	    String nome,
	    boolean imprimeCozinha,
	    Integer categoriaId,
	    String categoriaNome,
	    List<PrecoTamanhoResponse> precos
		
		) {
	
	/**
     * Converte uma entidade Produto para o DTO de resposta.
     * Deve ser chamado dentro de uma transação ativa (ex: no próprio service).
     */
    public static ProdutoResponse from(Produto produto) {
        List<PrecoTamanhoResponse> precos = produto.getPrecosVariaveis().stream()
            .map(pv -> new PrecoTamanhoResponse(
                pv.getTamanho().getId(),
                pv.getTamanho().getNome(),
                pv.getValor()
            ))
            .collect(Collectors.toList());

        return new ProdutoResponse(
            produto.getId(),
            produto.getNome(),
            produto.isImprimeCozinha(),
            produto.getCategoria().getId(),
            produto.getCategoria().getNome(),
            precos
        );
    }
}
