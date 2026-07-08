package com.dsys.appfood.controller;

import java.net.URI;
import java.util.stream.Stream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.dsys.appfood.dto.request.EnderecoRequest;
import com.dsys.appfood.dto.response.EnderecoResponse;
import com.dsys.appfood.service.EnderecoService;

import jakarta.validation.Valid;

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
	@PostMapping
	public ResponseEntity<EnderecoResponse> cadastrar(@RequestBody @Valid EnderecoRequest request,
			UriComponentsBuilder uriBuilder) {

		EnderecoResponse response = enderecoService.cadastrarEnderecoResponse(request);
		// 1. Chamar o Service para executar as regras de negócio e salvar

		URI uri = uriBuilder.path("/api/enderecos/{id}").buildAndExpand(response.id()).toUri();
		// 2. Retornar o código 201 (Created) e a URL do novo recurso

		return ResponseEntity.created(uri).body(response);
		// 3. Devolver o DTO de Saída (Response)
	}

	// ====================================================================================================
	// 2. EDITAR ENDERECO
	// ====================================================================================================
	@PutMapping("/{id}")
	public ResponseEntity<EnderecoResponse> atualizar(@PathVariable Integer id,
									@RequestBody @Valid EnderecoRequest request ){

		EnderecoResponse response = enderecoService.editarEnderecoResponse(id, request);

		return ResponseEntity.ok(response);

	}


	// ====================================================================================================
	// 5. BUSCAS
	// ====================================================================================================
	@GetMapping
	public ResponseEntity<Page<EnderecoResponse>> buscarEndereco (
			@RequestParam(required = false) String logradouro,
			@RequestParam(required = false) String bairro,
			@RequestParam(required = false) String cep,
			Pageable pageable){

		// Contar quantos filtros foram enviados na reqisição
		long parametrosInformados = Stream.of(logradouro, bairro, cep)
				.filter(param -> param != null && !param.isBlank())
				.count();

		// Se informou mais de um filtro, rejeita imediatamente com 400 Bad Request
	    if (parametrosInformados > 1) {
	        throw new IllegalArgumentException("Permitido filtrar por apenas um critério por vez (logradouro, bairro OU cep).");
	    }

		Page<EnderecoResponse> paginaResultados;

		if(logradouro != null) {

			paginaResultados = enderecoService.listarEnderecosPorLogradouroResponse(logradouro, pageable);

		} else if (bairro != null) {

			paginaResultados = enderecoService.listarEnderecosPorBairroResponse(bairro, pageable);

		} else if (cep != null) {

			paginaResultados = enderecoService.listarEnderecosPorCepResponse(cep, pageable);

		}else {

			paginaResultados = enderecoService.listarTodosOsEnderecosResponse(pageable);

		}

		return ResponseEntity.ok(paginaResultados);

	}

}
