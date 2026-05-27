package com.dsys.appfood.controller;

import com.dsys.appfood.service.MesaService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mesas")
public class MesaController {
	
	private final MesaService mesaService;

	public MesaController(MesaService mesaService) {
		
		this.mesaService = mesaService;
		
	}
	
	// ====================================================================================================
	// 1. CADASTRAR NOVA MESA
	// ====================================================================================================
	
		// 1. Chamar o Service para executar as regras de negócio e salvar
	
		// 2. Retornar o código 201 (Created) e a URL do novo recurso
	
		// 3. Devolver o DTO de Saída (Response)
	
	// ====================================================================================================
	// 2. EDITAR MESA
	// ====================================================================================================
	
	
	// ====================================================================================================
	// 3. ATIVAR/INATIVAR MESA 
	// ====================================================================================================
	
	
	// ====================================================================================================
	// 4. BUSCAR MESA POR ID
	// ====================================================================================================

	
	
	// ====================================================================================================
	// 5. BUSCAR MESA POR NUMERO E LISTA 
	// ====================================================================================================

}
