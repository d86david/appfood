package com.dsys.appfood.dto.response;

import java.math.BigDecimal;

import com.dsys.appfood.domain.model.SessaoCaixa;

public record SessaoCaixaStatusResponse(

		boolean aberto,
		Integer sessaoCaixaId,
		Integer caixaId,
		String caixaNome,
		String operador,
		BigDecimal saldoAtual
		) {

	/**
     * Fábrica para quando tem Sessão de caixa aberta
     */
	public static SessaoCaixaStatusResponse deSessaoAberta(SessaoCaixa sessaoCaixa) {
		return new SessaoCaixaStatusResponse(
				true,
				sessaoCaixa.getId(),
				sessaoCaixa.getCaixa().getId(),
				sessaoCaixa.getCaixa() != null ? sessaoCaixa.getCaixa().getNome() : null,
				sessaoCaixa.getOperador() != null ? sessaoCaixa.getOperador().getNome() : null,
				sessaoCaixa.getSaldo()
				);
	}

	 /**
     * Fábrica para quando não há Sessão de caixa aberta
     */

	public static SessaoCaixaStatusResponse vazio() {
		return new SessaoCaixaStatusResponse(false, null,null,null,null, BigDecimal.ZERO);
	}

}
