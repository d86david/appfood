package com.dsys.appfood.dto.response;

import com.dsys.appfood.domain.enums.TipoDocumento;
import com.dsys.appfood.domain.model.Cliente;
import com.dsys.appfood.domain.model.Endereco;

public record ClienteResponse(

		Integer id,
		String nome,
		String telefonePrincipal,
		String telefoneSecundario,
		Boolean ativo,
		TipoDocumento tipoDocumento,
		String documento,
		String email,
		String observacaoCliente,
		Integer enderecoId,
		String enderecoLogradouro // Para facilitar a visualização
		) {
	public static ClienteResponse from (Cliente cliente) {
		Endereco end = cliente.getEndereco();
		return new ClienteResponse(
				cliente.getId(),
				cliente.getNome(),
				cliente.getTelefonePrincipal(),
				cliente.getTelefoneSecundario(),
				cliente.isAtivo(),
				cliente.getTipoDocumento(),
				cliente.getDocumento(),
				cliente.getEmail(),
				cliente.getObservacaoCliente(),
				end != null ? end.getId() : null,
				end != null ? end.getLogradouro() : null
				);
	}
}
