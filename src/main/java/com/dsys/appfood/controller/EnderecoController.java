package com.dsys.appfood.controller;

import com.dsys.appfood.service.EnderecoService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/enderecos")
public class EnderecoController {
	
	private final EnderecoService enderecoService;

	public EnderecoController(EnderecoService enderecoService) {
		
		this.enderecoService = enderecoService;
		
	}
	
	
	// ====================================================================================================
	// 1. CADASTRAR NOVO ENDERECO
	// ====================================================================================================
	
		// 1. Chamar o Service para executar as regras de negócio e salvar
	
		// 2. Retornar o código 201 (Created) e a URL do novo recurso
	
		// 3. Devolver o DTO de Saída (Response)
	
	// ====================================================================================================
	// 2. EDITAR ENDERECO
	// ====================================================================================================
	
	
	// ====================================================================================================
	// 3. EXCLUIR ENDERECO
	// ====================================================================================================
	
	
	// ====================================================================================================
	// 4. BUSCAR ENDERECO POR ID
	// ====================================================================================================

	
	// ====================================================================================================
	// 5. BUSCAR POR ENDERECO E LISTA 
	// ====================================================================================================


}
