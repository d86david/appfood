package com.dsys.appfood.dto.response;

import java.math.BigDecimal;

import com.dsys.appfood.domain.enums.TipoConta;
import com.dsys.appfood.domain.model.ContaCorrente;

public record ContaCorrenteResponse(

		Integer id,
		String nome,
		TipoConta tipo,
		String codBanco,
		String banco,
		String agencia,
		String conta,
		BigDecimal saldoAtual,
		boolean ativa

) {
	public static ContaCorrenteResponse from(ContaCorrente cc) {
		return new ContaCorrenteResponse(
				cc.getId(),
				cc.getNome(),
				cc.getTipo(),
				cc.getCodBanco(),
				cc.getBanco(),
				cc.getAgencia(),
				cc.getConta(),
				cc.getSaldoAtual(),
				cc.isAtiva()
				);
	}
}
