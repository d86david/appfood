package com.dsys.appfood.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dsys.appfood.domain.model.Tamanho;
import com.dsys.appfood.dto.request.TamanhoRequest;
import com.dsys.appfood.dto.response.TamanhoResponse;
import com.dsys.appfood.exception.NegocioException;
import com.dsys.appfood.exception.TamanhoJaCadastradoException;
import com.dsys.appfood.exception.TamanhoNaoEncontradoException;
import com.dsys.appfood.repository.PrecoVariavelRepository;
import com.dsys.appfood.repository.TamanhoRepository;

import jakarta.annotation.PostConstruct;

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

	// ======================================================================
    // MÉTODO DE INICIALIZAÇÃO - Executado UMA VEZ na inicialização do Spring
    // ======================================================================
	/**
	 * Esse método garante que o tamanho "ÚNICO" (ou PADRÃO) exista no banco de dados
	 * para ser usado por produtos que não possuem variação de tamanho
	 *
	 * O @PostConstruct é executado automaticamente pelo Spring após a injeção
	 * de dependencias e ANTES da aplicação começar receber requisições
	 *
	 * CONCEITO: Inicialização de dados mestres (Master Data).
	 * Útil para cadastros fixos que o sistema sempre vai precisar
	 */
	@PostConstruct
	@Transactional
	public void initTamanhoUnico() {

		//Verifica se já existe um tamanho com o nome "ÚNICO" (case insensitive)
		boolean existe = tamanhoRepository.findByNomeIgnoreCase("ÚNICO").isPresent();

		if (!existe) {
			//Cria e salva o tamanho "ÚNICO"
			Tamanho unico = new Tamanho("ÚNICO");
			tamanhoRepository.save(unico);
			System.out.println(">>> Tamanho 'ÚNICO' criado com sucesso no banco de dados");
		}else {
			System.out.println(">>> Tamanho 'ÚNICO' ja existe.");
		}

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
	public Page<Tamanho> listarTodosTamanhos(Pageable pageable){
		return tamanhoRepository.findAll(pageable);
	}

	// Buscar por nome
	@Transactional(readOnly = true)
	public Page<Tamanho> buscarTamanhoPorNome(String nome, Pageable pageable){

		//Validações
		if(nome == null || nome.isBlank()) {
			throw new IllegalArgumentException("O tamanho deve ser informado");
		}

		return tamanhoRepository.findByNomeContainingIgnoreCase(nome, pageable);

	}

	// Buscar Tamanho "ÚNICO"
		@Transactional(readOnly = true)
		public Tamanho buscarTamanhoUnico(String nome){

			//Validações
			if(nome == null || nome.isBlank()) {
				throw new IllegalArgumentException("O tamanho deve ser informado");
			}

			return tamanhoRepository.findByNomeIgnoreCase(nome).orElseThrow(() -> new TamanhoNaoEncontradoException(null));

		}


	// =============================================================
	//  MÉTODOS DTO (conversão dentro da transação)
	// =============================================================

	@Transactional
	public TamanhoResponse cadastrarTamanhoResponse(TamanhoRequest request) {

		Tamanho tamanho = cadastrarTamanho(request.nome());

		return TamanhoResponse.from(tamanho);

	}

	@Transactional
	public TamanhoResponse editarTamanhoResponse(Integer id, TamanhoRequest request) {

		Tamanho tamanhoAtualizado = editarTamanho(id, request.nome());

		return TamanhoResponse.from(tamanhoAtualizado);

	}

	@Transactional(readOnly = true)
	public TamanhoResponse buscarPorIdResponse(Integer id) {

		return TamanhoResponse.from(buscarPorId(id));

	}

	@Transactional(readOnly = true)
	public Page<TamanhoResponse> buscarPorNomeResponse(String nome, Pageable pageable) {

		return buscarTamanhoPorNome(nome, pageable).map(TamanhoResponse::from);

	}

	@Transactional(readOnly = true)
	public Page<TamanhoResponse> listarTodasOsTamanhosResponse(Pageable pageable){

		return listarTodosTamanhos(pageable).map(TamanhoResponse::from);
	}

}
