package com.dsys.appfood.service;

import com.dsys.appfood.domain.model.Impressora;
import com.dsys.appfood.domain.model.ImpressoraCanal;
import com.dsys.appfood.domain.model.CanalImpressao;
import com.dsys.appfood.dto.request.ImpressoraCanalRequest;
import com.dsys.appfood.dto.response.ImpressoraCanalResponse;
import com.dsys.appfood.exception.EntidadeNaoEncontradaException;
import com.dsys.appfood.exception.NegocioException;
import com.dsys.appfood.repository.ImpressoraCanalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * ======================================================================
 *  SERVICE: IMPRESSORA-CANAL (MAPEAMENTO)
 * ======================================================================
 * 
 * RESPONSABILIDADE ÚNICA: Gerenciar o mapeamento entre impressoras e canais.
 * 
 * Este serviço cuida APENAS do relacionamento entre Impressoras e Canais.
 * Ele não sabe como formatar um cupom, apenas quem imprime o quê.
 */
@Service
public class ImpressoraCanalService {
    private final ImpressoraCanalRepository impressoraCanalRepository;
    private final ImpressoraService impressoraService;
    private final CanalImpressaoService canalService;

    public ImpressoraCanalService(
            ImpressoraCanalRepository impressoraCanalRepository,
            ImpressoraService impressoraService,
            CanalImpressaoService canalService) {
        this.impressoraCanalRepository = impressoraCanalRepository;
        this.impressoraService = impressoraService;
        this.canalService = canalService;
    }

    // ===========================================
    // VINCULAR IMPRESSORA A CANAL
    // ===========================================
    /**
     * Cria um mapeamento entre uma impressora e um canal.
     * 
     * CONCEITO: Validação de Integridade
     * - Verifica se a impressora existe e está ativa
     * - Verifica se o canal existe e está ativo
     * - Verifica se já não existe esse mapeamento (evita duplicata)
     */
    @Transactional
    public ImpressoraCanal vincular(Integer impressoraId, Integer canalId) {
        // Busca as entidades (lança exceção se não existirem)
        Impressora impressora = impressoraService.buscarPorId(impressoraId);
        CanalImpressao canal = canalService.buscarPorId(canalId);
        
        // Validações de estado
        if (!impressora.isAtiva()) {
            throw new NegocioException("Não é possível vincular uma impressora inativa");
        }
        if (!canal.isAtivo()) {
            throw new NegocioException("Não é possível vincular um canal inativo");
        }
        
        // Validação de duplicata
        if (impressoraCanalRepository.existsByImpressoraIdAndCanalId(impressoraId, canalId)) {
            throw new NegocioException("Este canal já está vinculado a esta impressora");
        }
        
        ImpressoraCanal mapeamento = new ImpressoraCanal(impressora, canal);
        return impressoraCanalRepository.save(mapeamento);
    }

    // ===========================================
    // DESVINCULAR (Inativação Lógica)
    // ===========================================
    @Transactional
    public void desvincular(Integer impressoraId, Integer canalId) {
        // Busca o mapeamento
        ImpressoraCanal mapeamento = impressoraCanalRepository.findAll().stream()
            .filter(ic -> ic.getImpressora().getId().equals(impressoraId) 
                       && ic.getCanal().getId().equals(canalId))
            .findFirst()
            .orElseThrow(() -> new EntidadeNaoEncontradaException(
                "Mapeamento não encontrado entre impressora " + impressoraId + " e canal " + canalId));
        
        mapeamento.setAtivo(false);
        impressoraCanalRepository.save(mapeamento);
    }

    // ===========================================
    // CONSULTAS
    // ===========================================
    /**
     * Busca a impressora responsável por um canal específico.
     * 
     * CONCEITO: Método de Conveniência
     * Este método é usado pelo ImpressaoService para descobrir
     * para qual impressora enviar o conteúdo de um canal.
     * 
     * @param nomeCanal Nome do canal (ex: "BALCAO", "PIZZAS")
     * @return Impressora responsável, ou null se não houver mapeamento
     */
    @Transactional(readOnly = true)
    public Impressora buscarImpressoraPorCanal(String nomeCanal) {
        return impressoraCanalRepository.findByCanalNomeAtivo(nomeCanal)
            .map(ImpressoraCanal::getImpressora)
            .orElse(null);
    }

    @Transactional(readOnly = true)
    public List<ImpressoraCanal> listarPorImpressora(Integer impressoraId) {
        return impressoraCanalRepository.findByImpressoraIdAtivo(impressoraId);
    }

	// =============================================================
	//  MÉTODOS DTO (conversão dentro da transação)
	// =============================================================
    @Transactional
    public ImpressoraCanalResponse vincularResponse(ImpressoraCanalRequest request) {
    	
    	ImpressoraCanal impressoraCanal = vincular(request.impressoraId(), request.canalId());
    	
    	return ImpressoraCanalResponse.from(impressoraCanal);
    }
    
    @Transactional(readOnly = true)
    public List<ImpressoraCanalResponse> listarPorImpressoraResponse(Integer impressoraId) {
    	
    	return listarPorImpressora(impressoraId).stream()
    			.map(ImpressoraCanalResponse::from)
    			.toList();
    	
    }

}
