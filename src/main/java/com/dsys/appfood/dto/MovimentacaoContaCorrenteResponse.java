package com.dsys.appfood.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.dsys.appfood.domain.enums.TipoMovimentacao;
import com.dsys.appfood.domain.model.MovimentacaoContaCorrente;

public record MovimentacaoContaCorrenteResponse(	
		Integer id,
		TipoMovimentacao tipo,
		BigDecimal valor,
		String descricao,
		LocalDateTime dataHora,
		Integer pagamentoId,
		String usuarioNome
		) {
	public static MovimentacaoContaCorrenteResponse from (MovimentacaoContaCorrente mov) {
		return new MovimentacaoContaCorrenteResponse(
				mov.getId(),
				mov.getTipo(),
				mov.getValor(),
				mov.getDescricao(),
				mov.getDataHora(),
				mov.getPagamento() != null ? mov.getPagamento().getId() : null,
				mov.getUsuario() != null ? mov.getUsuario().getNome() : null
				);
	}
}
