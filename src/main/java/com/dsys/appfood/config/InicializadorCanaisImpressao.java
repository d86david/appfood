package com.dsys.appfood.config;

import com.dsys.appfood.domain.model.CanalImpressao;
import com.dsys.appfood.repository.CanalImpressaoRepository;

import jakarta.annotation.PostConstruct;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * ======================================================================
 *  INICIALIZADOR: CANAIS DE IMPRESSÃO PADRÃO
 * ======================================================================
 * 
 * CONCEITO: Master Data Initialization
 * 
 * Este componente é executado UMA VEZ na inicialização da aplicação
 * e garante que os canais padrão existam no banco de dados.
 * 
 * É idempotente: se os canais já existirem, não faz nada.
 */
@Component
public class InicializadorCanaisImpressao {
	
	private final CanalImpressaoRepository canalRepository;

	public InicializadorCanaisImpressao(CanalImpressaoRepository canalRepository) {
		
		this.canalRepository = canalRepository;
		
	}
	
	@PostConstruct
	@Transactional
	public void inicializarCanaisPadrao() {
		
		criarSeNaoExistir("BALCAO", "Cupom do cliente com valores e dados pessoais");
        criarSeNaoExistir("PIZZAS", "Pedido do forno de pizzas (sem valores)");
        criarSeNaoExistir("LANCHES", "Pedido da chapa de lanches");
        criarSeNaoExistir("BEBIDAS", "Pedido do bar");
        criarSeNaoExistir("COZINHA", "Cupom geral da cozinha");
        
        System.out.println(">>> Canais de impressão padrão inicializados");
	}
	
	private void criarSeNaoExistir(String nome, String descricao) {
		
		if(canalRepository.findByNomeIgnoreCase(nome).isEmpty()) {
			CanalImpressao canal = new CanalImpressao(nome, descricao);
            canalRepository.save(canal);
		}
		
	}

}
