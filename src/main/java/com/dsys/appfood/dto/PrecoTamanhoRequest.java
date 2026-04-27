package com.dsys.appfood.dto;

import java.math.BigDecimal;

//=========================================================
// DTO INTERNO - Agrupa Tamanho + Valor para o Service
// Não vai para o banco, só transporta dados entre métodos
//=========================================================
public record PrecoTamanhoRequest (
	
	Integer tamanhoId,
	BigDecimal valor
	
	){}