package com.dsys.appfood.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.dsys.appfood.domain.enums.StatusCaixa;
import com.dsys.appfood.domain.model.Caixa;

public record CaixaResponse(
		
		Integer id,	
	    StatusCaixa status,
	    BigDecimal saldo,
	    BigDecimal valorInicial,
	    LocalDateTime dataAbertura,
	    LocalDateTime dataFechamento,
	    String operadorNome,
	    String gerenteNome
		
		) {
	
	public static CaixaResponse from (Caixa caixa) {
		return new CaixaResponse(
				caixa.getId(),
				caixa.getStatus(),
				caixa.getSaldo(),
				caixa.getValorInicial(),
				caixa.getDataAbertura(),
				caixa.getDataFechamento(),
				caixa.getOperador() != null ? caixa.getOperador().getNome() : null,
				caixa.getGerente() != null ? caixa.getGerente().getNome() : null
				);
	}

}
