package com.dsys.appfood.controller;

import com.dsys.appfood.service.EntregadorService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/entregadores")
public class EntregadorController {
	
	private final EntregadorService entregadorService;

	public EntregadorController(EntregadorService entregadorService) {
		
		this.entregadorService = entregadorService;
		
	}
	
	
	// ====================================================================================================
	// 1. CADASTRAR NOVO ENTREGADOR
	// ====================================================================================================
	
		// 1. Chamar o Service para executar as regras de negócio e salvar
	
		// 2. Retornar o código 201 (Created) e a URL do novo recurso
	
		// 3. Devolver o DTO de Saída (Response)
	
	// ====================================================================================================
	// 2. EDITAR ENTREGADOR
	// ====================================================================================================
	
	
	// ====================================================================================================
	// 3. ATIVAR/INATIVAR ENTREGADOR 
	// ====================================================================================================
	
	
	// ====================================================================================================
	// 4. BUSCAR ENTREGADOR POR ID
	// ====================================================================================================

	
	
	// ====================================================================================================
	// 5. BUSCAR ENTREGADOR POR NOME E LISTA 
	// ====================================================================================================


}
