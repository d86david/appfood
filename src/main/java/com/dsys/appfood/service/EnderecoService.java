package com.dsys.appfood.service;

import com.dsys.appfood.repository.EnderecoRepository;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dsys.appfood.domain.model.Endereco;
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
		String complementoPadronizado = complemento.trim();
		String bairroPadronizado = bairro.trim();
		String cidadePadronizado = cidade.trim();
		String referenciaPadronizada = pontoReferencia.trim();

		// MONTAR E SALVAR

		Endereco endereco = new Endereco(logradouroPadronizado, numeroPadronizado, complementoPadronizado,
				bairroPadronizado, cidadePadronizado, uf, cep,referenciaPadronizada );

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
		String complementoPadronizado = complementoNovo.trim();
		String bairroPadronizado = bairroNovo.trim();
		String cidadePadronizado = cidadeNova.trim();
		String referenciaPadronizada = novoPontoReferencia.trim();

		// BUSCA ENDEREÇO E LANÇA EXCEÇÃO SE NÃO EXISTIR
		Endereco endereco = enderecoRepository.findById(id).orElseThrow(() -> new EnderecoNaoEncontradoException(id));

		// MONTAR E SALVAR
		endereco.setLogradouro(logradouroPadronizado);
		endereco.setNumero(numeroPadronizado);
		endereco.setComplemento(complementoPadronizado);
		endereco.setBairro(bairroPadronizado);
		endereco.setCidade(cidadePadronizado);
		endereco.setUf(ufNovo);
		endereco.setCep(cepNovo);
		endereco.setPontoReferencia(referenciaPadronizada);

		return enderecoRepository.save(endereco);

	}

	// =============================================================
	// BUSCAR
	// ============================================================

	// Listar todos os Enderecos
	@Transactional(readOnly = true)
	public List<Endereco> listarTodosOsEnderecos() {
		return enderecoRepository.findAll();
	}

	// Listar enderecos por bairro
	@Transactional(readOnly = true)
	public List<Endereco> listarEnderecosPoBairro() {
		return enderecoRepository.findByBairroContainingIgnoreCase(null);
	}

}