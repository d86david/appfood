package com.dsys.appfood.dto.response;

import com.dsys.appfood.domain.model.Endereco;

public record EnderecoResponse(

		Integer id,
		String logradouro,
		String numero,
		String complemento,
		String bairro,
		String cidade,
		String uf,
		String cep,
		String pontoReferencia

		) {

	public static EnderecoResponse from(Endereco endereco){

		return new EnderecoResponse(
				endereco.getId(),
				endereco.getLogradouro(),
				endereco.getNumero(),
				endereco.getComplemento(),
				endereco.getBairro(),
				endereco.getCidade(),
				endereco.getUf(),
				endereco.getCep(),
				endereco.getPontoReferencia()
				);

	}

}
