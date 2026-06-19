package com.dsys.appfood.service;

import com.dsys.appfood.repository.EnderecoRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dsys.appfood.domain.model.Endereco;
import com.dsys.appfood.dto.request.EnderecoRequest;
import com.dsys.appfood.dto.response.EnderecoResponse;
import com.dsys.appfood.exception.EnderecoNaoEncontradoException;

/**
 * Classe responsavel pela autenticação e validações relacionadas ao Endereco
 * 
 * Responsabilidade ÚNICA: autenticação e validações relacionadas de endereços
 * 
 * Este Service NÃO sabe nada sobre HTTP, Apenas processa e lança exceções de
 * negócio.
 */
@Service
public class EnderecoService {

	private final EnderecoRepository enderecoRepository;

	public EnderecoService(EnderecoRepository enderecoRepository) {
		this.enderecoRepository = enderecoRepository;
	}

	// =============================================================
	// CADASTRAR
	// =============================================================

	@Transactional
	public Endereco cadastrarEndereco(String logradouro, String numero, String complemento, String bairro,
			String cidade, String uf, String cep, String pontoReferencia) {

		// VALIDAÇÕES SEM BANCO
		if (logradouro == null || logradouro.isBlank()) {
			throw new IllegalArgumentException("O logradouro deve ser informado.");
		}

		if (numero == null || numero.isBlank()) {
			throw new IllegalArgumentException("O numero deve ser informado");
		}

		if (bairro == null || bairro.isBlank()) {
			throw new IllegalArgumentException("O bairro deve ser informado");
		}

		// Padroniza os nomes
		String logradouroPadronizado = logradouro.trim();
		String numeroPadronizado = numero.trim();
		String bairroPadronizado = bairro.trim();

		// MONTAR E SALVAR

		Endereco endereco = new Endereco(logradouroPadronizado, numeroPadronizado, complemento,
				bairroPadronizado, cidade, uf, cep,pontoReferencia );

		return enderecoRepository.save(endereco);

	}

	// =============================================================
	// EDITAR
	// =============================================================

	@Transactional
	public Endereco editarEndereco(Integer id, String logradouroNovo, String numeroNovo, String complementoNovo,
			String bairroNovo, String cidadeNova, String ufNovo, String cepNovo, String novoPontoReferencia) {

		// VALIDAÇÕES SEM BANCO
		if (logradouroNovo == null || logradouroNovo.isBlank()) {
			throw new IllegalArgumentException("O logradouro deve ser informado.");
		}

		if (numeroNovo == null || numeroNovo.isBlank()) {
			throw new IllegalArgumentException("O numero deve ser informado");
		}

		if (bairroNovo == null || bairroNovo.isBlank()) {
			throw new IllegalArgumentException("O bairro deve ser informado");
		}

		// Padroniza os nomes
		String logradouroPadronizado = logradouroNovo.trim();
		String numeroPadronizado = numeroNovo.trim();
		String bairroPadronizado = bairroNovo.trim();

		// BUSCA ENDEREÇO E LANÇA EXCEÇÃO SE NÃO EXISTIR
		Endereco endereco = enderecoRepository.findById(id).orElseThrow(() -> new EnderecoNaoEncontradoException(id));

		// MONTAR E SALVAR
		endereco.setLogradouro(logradouroPadronizado);
		endereco.setNumero(numeroPadronizado);
		endereco.setComplemento(complementoNovo);
		endereco.setBairro(bairroPadronizado);
		endereco.setCidade(cidadeNova);
		endereco.setUf(ufNovo);
		endereco.setCep(cepNovo);
		endereco.setPontoReferencia(novoPontoReferencia);

		return enderecoRepository.save(endereco);

	}

	// =============================================================
	// BUSCAR
	// ============================================================

	// Listar enderecos por logradouro
	@Transactional(readOnly = true)
	public Page<Endereco> listarEnderecoPorLogradouro(String logradouro, Pageable pageable){
		
		if(logradouro == null || logradouro.isBlank()) {
			throw new IllegalArgumentException("O endereço deve ser informado.");
		}
		
		return enderecoRepository.findByLogradouroContainingIgnoreCase(logradouro, pageable);
		
	}
	
	// Listar enderecos por CEP
	@Transactional(readOnly = true)
	public Page<Endereco> listarEnderecoPorCep(String cep, Pageable pageable){
		
		if(cep == null || cep.isBlank()) {
			throw new IllegalArgumentException("O endereço deve ser informado.");
		}
		
		return enderecoRepository.findByCepContainingIgnoreCase(cep, pageable);
		
	}
	
	// Listar enderecos por bairro
	@Transactional(readOnly = true)
	public Page<Endereco> listarEnderecosPorBairro(String bairro, Pageable pageable) {
		if (bairro == null || bairro.isBlank()) {
	        throw new IllegalArgumentException("Bairro deve ser informado.");
	    }
	    return enderecoRepository.findByBairroContainingIgnoreCase(bairro, pageable);
	}
	
	// Listar todos os Enderecos
	@Transactional(readOnly = true)
	public Page<Endereco> listarTodosOsEnderecos(Pageable pageable) {
		return enderecoRepository.findAll(pageable);
	}
	
	// =============================================================
	//  MÉTODOS DTO (conversão dentro da transação)
	// =============================================================
	
	@Transactional
	public EnderecoResponse cadastrarEnderecoResponse(EnderecoRequest request) {
		
		Endereco endereco = cadastrarEndereco(request.logradouro(), request.numero(), 
				request.complemento(), request.bairro(), request.cidade(), request.uf(), request.cep(), request.pontoReferencia());
		
		return EnderecoResponse.from(endereco);
		
	}
	
	@Transactional
	public EnderecoResponse editarEnderecoResponse(Integer id, EnderecoRequest request) {
		
		Endereco enderecoAtualizado = editarEndereco(id, request.logradouro(), request.numero(), 
				request.complemento(), request.bairro(), request.cidade(), request.uf(), request.cep(), request.pontoReferencia());
		
		return EnderecoResponse.from(enderecoAtualizado);
		
	}
	
	@Transactional(readOnly = true)
	public Page<EnderecoResponse> listarEnderecosPorLogradouroResponse(String logradouro, Pageable pageable){
		
		return listarEnderecoPorLogradouro(logradouro, pageable)
				.map(EnderecoResponse::from);
	}
	
	@Transactional(readOnly = true)
	public Page<EnderecoResponse> listarEnderecosPorCepResponse(String cep, Pageable pageable){
		
		return listarEnderecoPorCep(cep, pageable)
				.map(EnderecoResponse::from);
		
	}
	
	@Transactional(readOnly = true)
	public Page<EnderecoResponse> listarEnderecosPorBairroResponse(String bairro, Pageable pageable){
		
		return listarEnderecosPorBairro(bairro, pageable)
				.map(EnderecoResponse::from);
		
	}
	
	@Transactional(readOnly = true)
	public Page<EnderecoResponse> listarTodosOsEnderecosResponse(Pageable pageable){
		
		return listarTodosOsEnderecos(pageable)
				.map(EnderecoResponse::from);
		
	}

}