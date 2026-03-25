package com.dsys.appfood.service;

import com.dsys.appfood.repository.ProdutoRepository;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dsys.appfood.domain.model.Categoria;
import com.dsys.appfood.repository.CategoriaRepository;

/**
 * Classe responsavel por gerenciar as Categorias do cardápio.
 * 
 * Responsabilidade ÚNICA: regras de negócio relacionadas a Categoria
 * 
 * Este Service NÃO sabe nada sobre HTTP, 
 * Apenas processa e lança exceções de negócio.
 */
@Service
public class CategoriaService {
	
	private final ProdutoRepository produtoRepository;
	private final CategoriaRepository categoriaRepository;
	
	public CategoriaService(CategoriaRepository categoriaRepository, ProdutoRepository produtoRepository) {
		
		this.categoriaRepository = categoriaRepository;
		this.produtoRepository = produtoRepository;
	}

	//=============================================================
	// CADASTRO
	//=============================================================
	
	@Transactional
	public Categoria cadastrarCategoria(String nome ) {
		
		// Verifica se o nome da Catgoria está em Branco
		if(nome == null || nome.isBlank()) {
			throw new IllegalArgumentException("O nome da categoria não pode ser vazio.");
		}
		
		
		//Padroniza o nome antes de verificar duplicata e salvar
		// "   Pizzas   " vira "pizzas"
		String nomePadronizado = nome.trim();
		
		// Verifica se a Categoria ja existe no banco
		if(categoriaRepository.findByNomeIgnoreCase(nomePadronizado).isPresent()) {
			throw new IllegalStateException("Já existe uma categoria com o nome: " + nomePadronizado);
		}
		
		// Tudo validado — agora sim cria e salva
		Categoria categoria = new Categoria(nomePadronizado);
		return categoriaRepository.save(categoria);
	}
	
	//=============================================================
	// EDIÇÃO
	//=============================================================
	@Transactional
	public Categoria editarCategoria(Integer id, String novoNome) {
		
		//Validação sem banco
		if(novoNome == null || novoNome.isBlank()) {
			throw new IllegalArgumentException(
					"O nome da categoria não pode ser vazio");
		}
		
		String nomePadronizado = novoNome.trim();
		
		//Busca a categoria - laça Exeção se não existir 
		Categoria categoria = categoriaRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException(
						"Categoria não encontrada: id " + id));
		
		// Verifica se outro registro ja usa esse nome
		// (ignora o próprio registro que está sendo editado)
		categoriaRepository.findByNomeIgnoreCase(nomePadronizado)
			.ifPresent(existente -> {
				if(!existente.getId().equals(id)) {
					throw new IllegalStateException(
							"Ja existe uma categoria como nome: " + nomePadronizado);
				}
			});
		
		categoria.setNome(nomePadronizado);
		return categoriaRepository.save(categoria);
	}
	
	
	//=============================================================
	// EXCLUSÃO
	//=============================================================
	@Transactional
	public void excluirCategoria (Integer id) {
		// Confirma que existe antes de tentar excluir 
		// Sem isso, deleteById lança exceção generiaca e confusa
		if(!categoriaRepository.existsById(id)) {
			throw new IllegalArgumentException("Categoria não encontrada: id "+ id);
		}
		
		// Verificar se a categoria tem produtos vinculados antes de excluir.
		if(produtoRepository.existsByCategoriaId(id)) {
			throw new IllegalArgumentException("Essa categoria não pode ser excluída!"
					+ "\nExistem produtos cadastrados");
		}
        // Excluir
		categoriaRepository.deleteById(id);
	}
	
	//=============================================================
	// LISTA - Carregar todas as Cadastradas
	//=============================================================
	@Transactional(readOnly = true)
	public List<Categoria> listarTodasCategorias(){
		return categoriaRepository.findAll();
	}
	
	@Transactional(readOnly = true)
    public Categoria buscarCategoriaPorId(Integer id) {
        return categoriaRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException(
                "Categoria não encontrada: id " + id));
    }
}
