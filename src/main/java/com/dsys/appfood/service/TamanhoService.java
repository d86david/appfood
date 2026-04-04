package com.dsys.appfood.service;

import com.dsys.appfood.repository.PrecoVariavelRepository;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dsys.appfood.domain.model.Tamanho;
import com.dsys.appfood.exception.NegocioException;
import com.dsys.appfood.exception.TamanhoJaCadastradoException;
import com.dsys.appfood.exception.TamanhoNaoEncontradoException;
import com.dsys.appfood.repository.TamanhoRepository;

/**
 * Classe responsavel por gerenciar os Tamanhos dos produtos .
 * 
 * Responsabilidade ÚNICA: regras de negócio relacionadas ao tamanho
 * 
 * Este Service NÃO sabe nada sobre HTTP, Apenas processa e lança exceções de
 * negócio.
 */
@Service
public class TamanhoService {

	private final PrecoVariavelRepository precoVariavelRepository;
	private final TamanhoRepository tamanhoRepository;

	public TamanhoService(TamanhoRepository tamanhoRepository, PrecoVariavelRepository precoVariavelRepository) {

		this.tamanhoRepository = tamanhoRepository;
		this.precoVariavelRepository = precoVariavelRepository;
	}

	// =============================================================
	// CADASTRO
	// =============================================================
	@Transactional
	public Tamanho cadastrarTamanho(String nome) {

		// Verifica se o nome da está em Branco
		if (nome == null || nome.isBlank()) {
			throw new IllegalArgumentException("O tamanho não pode ser vazio.");
		}

		// Padroniza o nome antes de verificar duplicata e salvar
		String nomePadronizado = nome.trim().toUpperCase();

		// Verifica se o tamanho ja existe no banco
		if (tamanhoRepository.findByNomeIgnoreCase(nomePadronizado).isPresent()) {
			throw new TamanhoJaCadastradoException(nomePadronizado);
		}

		// Tudo validado — cria e salva
		Tamanho tamanho = new Tamanho(nomePadronizado);

		return tamanhoRepository.save(tamanho);
	}

	// =============================================================
	// EDIÇÃO
	// =============================================================
	@Transactional
	public Tamanho editarTamanho(Integer id, String novoNome) {

		// Validação sem banco
		if (novoNome == null || novoNome.isBlank()) {
			throw new IllegalArgumentException("O tamanho não pode ser vazio.");
		}

		String nomePadronizado = novoNome.trim().toUpperCase();

		// Busca o tamanho - laça Exeção se não existir
		Tamanho tamanho = tamanhoRepository.findById(id)
				.orElseThrow(() -> new TamanhoNaoEncontradoException(id));

		// Verifica se outro registro ja usa esse nome
		tamanhoRepository.findByNomeIgnoreCase(nomePadronizado).ifPresent(existente -> {
			if (!existente.getId().equals(id)) {
				throw new TamanhoJaCadastradoException(nomePadronizado, id);
			}
		});
		
		tamanho.setNome(nomePadronizado);
		
		return tamanhoRepository.save(tamanho);

	}

	// =============================================================
	// EXCLUSÃO
	// =============================================================
	@Transactional
	public void excluirTamanho(Integer id) {
		// Confirma que existe antes de tentar excluir 
		if(!tamanhoRepository.existsById(id)) {
			throw new TamanhoNaoEncontradoException(id);
		}
		
		
		// Verificar se o tamanho tem produtos vinculados antes de excluir.
		if(precoVariavelRepository.existsByTamanhoId(id)) {
			throw new NegocioException("Esse Tamanho não pode ser excluído!"
					+ "\nExistem precos cadastrados");
		}
		
		tamanhoRepository.deleteById(id);
	}

	// =============================================================
	// BUSCAS
	// =============================================================

	// Buscar por ID
	@Transactional(readOnly = true)
	public Tamanho buscarPorId(Integer id) {
		return tamanhoRepository.findById(id)
				.orElseThrow(() -> new TamanhoNaoEncontradoException(id));
	}
	
	// Listar todos
	@Transactional(readOnly = true)
	public List<Tamanho> listarTodosTamanhos(){
		return tamanhoRepository.findAll();
	}
	
	// Buscar por nome
	@Transactional(readOnly = true)
	public List<Tamanho> buscarTamanhoPorNome(String nome){
		
		//Validações
		if(nome == null || nome.isBlank()) {
			throw new IllegalArgumentException("O tamanho deve ser informado");
		}
		
		return tamanhoRepository.findByNomeContainingIgnoreCase(nome);
		
	}

}
