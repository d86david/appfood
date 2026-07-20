package com.dsys.appfood.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AdicionarItemRequest(

		@NotNull(message = " O ID do tamanho é obrigatório")
		Integer tamanhoId,

		/**
		 * CAMPO OPCIONAL:  Se for um produto simples (bebida, sobremesa, etc), informe o ID do produto aqui.
		 * Se for Pizza, deixe null e use o endpoint /sabores para adicionar sabores.
		 */
		Integer produtoId,
		
		/**
	     * QUANTIDADE DO ITEM
	     * - Padrão: 1 (se não informado)
	     * - Mínimo: 1 (não pode ser zero ou negativo)
	     * - Exemplo: 2 pizzas, 3 refrigerantes
	     */
	    @Min(value = 1, message = "A quantidade deve ser no mínimo 1")
	    Integer quantidade

		) {
	
	/**
     * CONCEITO: Método de Conveniência
     * Retorna a quantidade informada ou 1 (padrão) se for nula.
     * Isso evita NullPointerException no Service.
     */
    public Integer getQuantidadeOrDefault() {
        return quantidade != null ? quantidade : 1;
    }
	
	
}
