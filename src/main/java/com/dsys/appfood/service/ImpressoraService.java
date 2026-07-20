package com.dsys.appfood.service;

import com.dsys.appfood.domain.model.Impressora;
import com.dsys.appfood.dto.request.ImpressoraRequest;
import com.dsys.appfood.dto.request.ImpressoraStatusRequest;
import com.dsys.appfood.dto.response.ImpressoraResponse;
import com.dsys.appfood.exception.ImpressoraJaCadastradaException;
import com.dsys.appfood.exception.ImpressoraNaoEncontradaException;
import com.dsys.appfood.repository.ImpressoraRepository;

import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ======================================================================
 *  SERVICE: IMPRESSORA
 * ======================================================================
 * 
 * RESPONSABILIDADE ÚNICA: Gerenciar o ciclo de vida das impressoras físicas.
 * 
 * Este Service NÃO sabe nada sobre roteamento de impressão.
 * Ele apenas faz CRUD de impressoras.
 */
@Service
public class ImpressoraService {
	
	private final ImpressoraRepository impressoraRepository;

	public ImpressoraService(ImpressoraRepository impressoraRepository) {
		
		this.impressoraRepository = impressoraRepository;
		
	}
	
	
    // ===========================================
    // CADASTRO
    // ===========================================
	@Transactional
	public Impressora cadastrar(String nome, Integer larguraColunas, String modelo, String portComunicacao) {
		
		// Validação Sem Banco
		if(nome == null || nome.isBlank()) {
			throw new IllegalArgumentException("O nome da impressora é obrigatório");
		}
		
		String nomePadronizado = nome.trim();
		
		// Validação de duplicata
		impressoraRepository.findByNomeIgnoreCase(nomePadronizado).ifPresent(existente ->{
			throw new ImpressoraJaCadastradaException(nomePadronizado);
		});
		
		Impressora impressora = new Impressora(nomePadronizado, larguraColunas);
		impressora.setModelo(modelo);
		impressora.setPortaComunicacao(portComunicacao);
		
		return impressoraRepository.save(impressora);
	}
	
	// ===========================================
    // EDIÇÃO
    // ===========================================
    @Transactional
    public Impressora editar (Integer id, String novoNome, Integer novaLargura, String novoModelo, String novaPorta) {
    	
    	Impressora impressora = buscarPorId(id);
    	
    	String nomePadronizado = novoNome.trim();
    	
    	//Validação de duplicata (Ignorando a propria impressora)
    	impressoraRepository.findByNomeIgnoreCase(nomePadronizado).ifPresent(existente -> {
    		if(!existente.getId().equals(id)) {
    			throw new ImpressoraJaCadastradaException(nomePadronizado, id);
    		}
    	});
    	
    	impressora.setNome(nomePadronizado);
    	impressora.setLarguraColunas(novaLargura);
    	impressora.setModelo(novoModelo);
    	impressora.setPortaComunicacao(novaPorta);
    	
    	return impressoraRepository.save(impressora);
    	
    }
    
	// =============================================================
	// ALTERAR STATUS
	// =============================================================
    @Transactional
    public void alterarStatus (Integer id, Boolean novoStatus) {
    	
    	Impressora impressoraStatus = buscarPorId(id);
    	
    	// Objects.equals previne NullPointerException se novoStatus ou impressoraStatus.isAtiva() forem null
	    // Só entra no bloco se o status for DIFERENTE do atual
    	if(!Objects.equals(impressoraStatus.isAtiva(), novoStatus)) {
    		impressoraStatus.setAtiva(novoStatus);
    	}
    	
    	impressoraRepository.save(impressoraStatus);
    	
    }
	

    // ===========================================
    // BUSCAS
    // ===========================================
    @Transactional(readOnly = true)
    public Impressora buscarPorId(Integer id) {
    	return impressoraRepository.findById(id)
    			.orElseThrow(() -> new ImpressoraNaoEncontradaException(id));
    }
    
    @Transactional(readOnly = true)
    public List<Impressora> listarAtivas() {
        return impressoraRepository.findByAtivaTrue();
    }

    @Transactional(readOnly = true)
    public Page<Impressora> listarTodas(Pageable pageable) {
        return impressoraRepository.findAll(pageable);
    }
    
	// =============================================================
	//  MÉTODOS DTO (conversão dentro da transação)
	// =============================================================
    
    @Transactional
    public ImpressoraResponse cadastrarResponse (ImpressoraRequest request) {
    	
    	Impressora impressora = cadastrar(
    			request.nome(), 
    			request.larguraColunas(), 
    			request.modelo(), 
    			request.portaComunicacao()
    			);
    	return ImpressoraResponse.from(impressora);
    }
    
    @Transactional
    public ImpressoraResponse editarResponse(Integer id, ImpressoraRequest request) {
    	Impressora impressora = editar(
    			id, 
    			request.nome(), 
    			request.larguraColunas(), 
    			request.modelo(), 
    			request.portaComunicacao()
    			);
    	return ImpressoraResponse.from(impressora);
    }
    
    @Transactional
    public void alterarStatusResponse(Integer id, ImpressoraStatusRequest request) {
    	
    	 alterarStatus(id, request.ativo());
    	
    }
    
    @Transactional(readOnly = true)
    public ImpressoraResponse buscarPorIdResponse(Integer id) {
        return ImpressoraResponse.from(buscarPorId(id));
    }
    
    @Transactional(readOnly = true)
    public Page<ImpressoraResponse> listarTodasResponse(Pageable pageable) {
        return listarTodas(pageable).map(ImpressoraResponse::from);
    }
}
