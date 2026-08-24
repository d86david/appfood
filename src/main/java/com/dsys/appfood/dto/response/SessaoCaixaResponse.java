package com.dsys.appfood.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.dsys.appfood.domain.enums.StatusCaixa;
import com.dsys.appfood.domain.model.SessaoCaixa;

public record SessaoCaixaResponse(

		Integer id, 
		Integer caixaId, 
		String caixaNome, 
		StatusCaixa status, 
		BigDecimal saldo,
		BigDecimal valorInicial, 
		LocalDateTime dataAbertura, 
		LocalDateTime dataFechamento, 
		String operadorNome,
		String gerenteNome

) {

	public static SessaoCaixaResponse from(SessaoCaixa sessaoCaixa) {
		return new SessaoCaixaResponse(
				sessaoCaixa.getId(), 
				sessaoCaixa.getCaixa().getId(),
				sessaoCaixa.getCaixa().getNome(), 
				sessaoCaixa.getStatus(), 
				sessaoCaixa.getSaldo(),
				sessaoCaixa.getValorInicial(),
				sessaoCaixa.getDataAbertura(), 
				sessaoCaixa.getDataFechamento(),
				sessaoCaixa.getOperador() != null ? sessaoCaixa.getOperador().getNome() : null,
				sessaoCaixa.getGerente() != null ? sessaoCaixa.getGerente().getNome() : null
		);
	}

}
