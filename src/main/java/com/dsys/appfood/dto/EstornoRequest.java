package com.dsys.appfood.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record EstornoRequest(

		@NotNull(message = "O ID do gerente é obrigatório")
		Integer gerenteId, 
		
		@Positive(message = "O valor deve ser positivo")
		BigDecimal valor, 
		
		@NotBlank(message = "O motivo é obrigatório")
		String motivo

) {}
