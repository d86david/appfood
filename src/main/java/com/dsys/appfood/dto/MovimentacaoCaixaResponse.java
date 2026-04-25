package com.dsys.appfood.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.dsys.appfood.domain.enums.TipoMovimentacao;
import com.dsys.appfood.domain.model.MovimentacaoCaixa;

public record MovimentacaoCaixaResponse(
		
		Integer id,
		TipoMovimentacao tipo,
		BigDecimal valor,
		String descricao,
		LocalDateTime dataHoraMovimento,
		String origem,
		String gerenteNome
		
		) {
	
	public static MovimentacaoCaixaResponse from(MovimentacaoCaixa mov) {
		return new MovimentacaoCaixaResponse(
				mov.getId(),
				mov.getTipo(),
				mov.getValor(),
				mov.getDescricao(),
				mov.getDataHoraMovimento(),
				mov.getOrigem(),
				mov.getGerente() != null ? mov.getGerente().getNome() : null
				);
	}

}
