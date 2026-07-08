package com.dsys.appfood.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ClienteRapidoRequest(

		@NotBlank(message = "O nome do cliente é obrigatório")
        String nome,

        @NotBlank(message = "O telefone principal é obrigatório")
        String telefonePrincipal,

        String observacaoCliente,

        @NotNull(message = "O ID do endereço é obrigatório")
        Integer enderecoId

		) {

}
