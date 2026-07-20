package com.dsys.appfood.service;

import com.dsys.appfood.domain.model.CanalImpressao;
import com.dsys.appfood.dto.request.CanalImpressaoRequest;
import com.dsys.appfood.dto.response.CanalImpressaoResponse;
import com.dsys.appfood.exception.CanalImpressaoJaCadastradoException;
import com.dsys.appfood.exception.CanalImpressaoNaoEncontradoException;
import com.dsys.appfood.repository.CanalImpressaoRepository;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ======================================================================
 *  SERVICE: CANAL DE IMPRESSORA
 * ======================================================================
 * 
 * RESPONSABILIDADE ÚNICA: Gerenciar o canal de impressão das impressoras.
 * 
 * Este Service NÃO sabe nada sobre roteamento de impressão.
 * Ele apenas faz CRUD dos canais de impressoras.
 */
@Service
public class CanalImpressaoService {
	
	private final CanalImpressaoRepository canalRepository;

	public CanalImpressaoService(CanalImpressaoRepository canalRepository) {
		
		this.canalRepository = canalRepository;
	}
	
	// =============================================================
	// CADASTRO
	// =============================================================
	@Transactional
	public CanalImpressao cadastrar (String nome, String descricao) {
		
		// Validações sem Banco
		if (nome == null || nome.isBlank()) {
			throw new IllegalArgumentException("O nome do canal é obrigatório");
		}
		
		String nomePadronizado = nome.trim().toUpperCase();
		
		canalRepository.findByNomeIgnoreCase(nomePadronizado).ifPresent(existente ->{
			throw new IllegalStateException("Já existe um canal com o nome: " + nomePadronizado);
		});
		
		CanalImpressao canal = new CanalImpressao(nomePadronizado, descricao);
		return canalRepository.save(canal);
	}
	
	// =============================================================
	// EDIÇÃO
	// ============================================================
	@Transactional
	public CanalImpressao editar (Integer id, String novoNome, String novaDescricao) {
		
		CanalImpressao canal = buscarPorId(id);
		
		String nomePadronizado = novoNome.trim().toUpperCase();
		
		canalRepository.findByNomeIgnoreCase(nomePadronizado).ifPresent(existente -> {
			if(!existente.getId().equals(id)) {
				throw new CanalImpressaoJaCadastradoException(nomePadronizado, id);
			}
		});
		
		canal.setNome(nomePadronizado);
		canal.setDescricao(novaDescricao);
		
		return canalRepository.save(canal);
	}
	
	// =============================================================
	// ALTERAR STATUS
	// =============================================================
	@Transactional
	public void alterarStatus(Integer id, Boolean novoStatus) {
		
		CanalImpressao canalStatus = buscarPorId(id);
		
		// Objects.equals previne NullPointerException se novoStatus ou impressoraStatus.isAtiva() forem null
	    // Só entra no bloco se o status for DIFERENTE do atual
		if(!Objects.equals(canalStatus.isAtivo(),novoStatus)) {
			canalStatus.setAtivo(novoStatus);
		}
	}
	
	// =============================================================
	// BUSCAS
	// =============================================================
	@Transactional (readOnly = true)
	public CanalImpressao buscarPorId(Integer id) {
		
		return canalRepository.findById(id)
				.orElseThrow(() -> new CanalImpressaoNaoEncontradoException(id));
	}
	
	@Transactional(readOnly = true)
	public CanalImpressao buscarPorNome(String nome) {
		
		return canalRepository.findByNomeIgnoreCase(nome)
				.orElseThrow(() -> new CanalImpressaoNaoEncontradoException(nome));
	}
	
	@Transactional(readOnly = true)
    public List<CanalImpressao> listarAtivos() {
        return canalRepository.findByAtivoTrue();
    }
	
	// =============================================================
	//  MÉTODOS DTO (conversão dentro da transação)
	// =============================================================
	@Transactional
	public CanalImpressaoResponse cadastrarResponse (CanalImpressaoRequest request) {
		
		CanalImpressao canal = cadastrar(request.nome(), request.descricao());
		
		return CanalImpressaoResponse.from(canal);
	}
	
	@Transactional
	public CanalImpressaoResponse editarResponse(Integer id, CanalImpressaoRequest request) {
		
		CanalImpressao canal = editar(id, request.nome(), request.descricao());
		
		return CanalImpressaoResponse.from(canal);
		
	}
	
	@Transactional(readOnly = true)
	public List<CanalImpressaoResponse> listarAtivosResponse(){
		
		return listarAtivos().stream().map(CanalImpressaoResponse:: from).toList();
		
	}
	
	@Transactional(readOnly = true)
    public CanalImpressaoResponse buscarPorIdResponse(Integer id) {
        return CanalImpressaoResponse.from(buscarPorId(id));
    }
}
