package com.dsys.appfood.service;

import com.dsys.appfood.domain.model.Caixa;
import com.dsys.appfood.dto.request.CaixaRequest;
import com.dsys.appfood.dto.response.CaixaResponse;
import com.dsys.appfood.exception.CaixaJaCadastradoException;
import com.dsys.appfood.exception.CaixaNaoEncontradoException;
import com.dsys.appfood.repository.CaixaRepository;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ======================================================================
 * SERVICE: CAIXA FÍSICO (CADASTRO)
 * ======================================================================
 * 
 * RESPONSABILIDADE ÚNICA: Gerenciar o cadastro de caixas físicos.
 * 
 * Este Service NÃO gerencia abertura/fechamento de sessões.
 * Isso é responsabilidade do MovimentoCaixaService.
 * 
 * CONCEITO: Separação de Responsabilidades
 * Cada Service cuida de um nível da hierarquia:
 * - CaixaFisicoService: cadastro (estático)
 * - MovimentoCaixaService: sessões (dinâmico)
 * - MovimentacaoCaixaService: eventos (imutável)
 */
@Service
public class CaixaService {
	
	private final CaixaRepository caixaRepository;

	public CaixaService(CaixaRepository caixaRepository) {
		this.caixaRepository = caixaRepository;
	}
	
	// ===========================================
    // CADASTRO
    // ===========================================
    
    /**
     * Cadastra um novo caixa físico
     * 
     * CONCEITO: Validação de Duplicidade
     * Garante que não haja dois caixas com o mesmo nome
     */
    @Transactional
    public Caixa cadastrar(String nome, String descricao, String localizacao) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome do caixa é obrigatório");
        }
        
        String nomePadronizado = nome.trim();
        
        // Validação de duplicata
        caixaRepository.findByNomeIgnoreCase(nomePadronizado).ifPresent(existente -> {
            throw new CaixaJaCadastradoException(nomePadronizado);
        });
        
        Caixa caixa = new Caixa(nomePadronizado, descricao, localizacao);
        return caixaRepository.save(caixa);
    }
    
    // ===========================================
    // EDIÇÃO
    // ===========================================
    
    /**
     * Edita um caixa físico existente
     */
    @Transactional
    public Caixa editar(Integer id, String novoNome, String novaDescricao, String novaLocalizacao) {
        Caixa caixa = buscarPorId(id);
        
        String nomePadronizado = novoNome.trim();
        
        // Validação de duplicata (ignorando o próprio registro)
        caixaRepository.findByNomeIgnoreCase(nomePadronizado).ifPresent(existente -> {
            if (!existente.getId().equals(id)) {
                throw new CaixaJaCadastradoException(nomePadronizado);
            }
        });
        
        caixa.setNome(nomePadronizado);
        caixa.setDescricao(novaDescricao);
        caixa.setLocalizacao(novaLocalizacao);
        
        return caixaRepository.save(caixa);
    }
    
	// =============================================================
	// ALTERAR STATUS
	// =============================================================
	@Transactional
	public void alterarStatus(Integer id, Boolean novoStatus) {
		Caixa caixaStatus = caixaRepository.findById(id)
				.orElseThrow(() -> new CaixaNaoEncontradoException(id));
		
		// Objects.equals previne NullPointerException se novoStatus ou usuarioStatus.isAtivo() forem null
	    // Só entra no bloco se o status for DIFERENTE do atual
		if(!Objects.equals(caixaStatus.isAtivo(), novoStatus)) {
			caixaStatus.setAtivo(novoStatus);
		}
		
		caixaRepository.save(caixaStatus);
	}
    
    
    // ===========================================
    // BUSCAS
    // ===========================================
    
    /**
     * Busca caixa físico por ID
     */
    @Transactional(readOnly = true)
    public Caixa buscarPorId(Integer id) {
        return caixaRepository.findById(id).orElseThrow(() -> new CaixaNaoEncontradoException(id));
    }
    
    /**
     * Lista todos os caixas físicos ativos
     */
    @Transactional(readOnly = true)
    public List<Caixa> listarAtivos() {
        return caixaRepository.findByAtivoTrue();
    }
    
    /**
     * Lista todos os caixas físicos
     */
    @Transactional(readOnly = true)
    public List<Caixa> listarTodos() {
        return caixaRepository.findAll();
    }
    
    
	// =============================================================
	//  MÉTODOS DTO (conversão dentro da transação)
	// =============================================================
    @Transactional
    public CaixaResponse cadastrarResponse(CaixaRequest request) {
        Caixa caixa = cadastrar(request.nome(), request.descricao(), request.localizacao());
        return CaixaResponse.from(caixa);
    }
    
    @Transactional
    public CaixaResponse editarResponse(Integer id, CaixaRequest request) {
        Caixa caixa = editar(id, request.nome(), request.descricao(), request.localizacao());
        return CaixaResponse.from(caixa);
    }
    
    @Transactional(readOnly = true)
    public CaixaResponse buscarPorIdResponse(Integer id) {
        return CaixaResponse.from(buscarPorId(id));
    }
    
    @Transactional(readOnly = true)
    public List<CaixaResponse> listarTodosResponse() {
    	
    	List<CaixaResponse> lista = caixaRepository.findAll().stream()
    			.map(CaixaResponse::from)
    			.toList();
    	return lista;
    }

}
