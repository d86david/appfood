package com.dsys.appfood.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dsys.appfood.domain.model.Tamanho;
import com.dsys.appfood.repository.TamanhoRepository;


/**
 * Classe responsavel por gerenciar os Tamanhos dos produtos .
 * 
 * Responsabilidade ÚNICA: regras de negócio relacionadas ao tamanho
 * 
 * Este Service NÃO sabe nada sobre HTTP, 
 * Apenas processa e lança exceções de negócio.
 */
@Service
public class TamanhoService {
	
	private final TamanhoRepository tamanhoRepository;
	
	public TamanhoService(TamanhoRepository tamanhoRepository) {
		
		this.tamanhoRepository = tamanhoRepository;
	}
	
	//=============================================================
	// CADASTRO
	//=============================================================
	
	
	
	//=============================================================
	// EDIÇÃO
	//=============================================================
	
	
	//=============================================================
	// EXCLUSÃO
	//=============================================================
	
	//=============================================================
	// LISTA - Carregar todos tamanhos
	//=============================================================
	
	
	
	@Transactional(readOnly = true)
    public Tamanho buscarPorId(Integer id) {
        return tamanhoRepository.findById(id)
        		.orElseThrow(() -> new IllegalArgumentException(
        				"Tamanho não encontrado " + id));
    }

}
