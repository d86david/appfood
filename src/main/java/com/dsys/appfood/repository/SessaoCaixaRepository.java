package com.dsys.appfood.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.dsys.appfood.domain.enums.StatusCaixa;
import com.dsys.appfood.domain.model.Caixa;
import com.dsys.appfood.domain.model.SessaoCaixa;
import com.dsys.appfood.domain.model.Usuario;

public interface SessaoCaixaRepository extends JpaRepository<SessaoCaixa, Integer> {
	
	//--- PESQUISA SESSÃO POR CAIXA ---
		List<SessaoCaixa> findByCaixa(Caixa caixa);
		
		//--- PESQUISA SESSÃO POR OPERADOR ---
		List<SessaoCaixa> findByOperador(Usuario operador);
		
		//--- PESQUISA SESSÕES POR PERÍDO ---
		List<SessaoCaixa> findByDataAberturaBetween(LocalDateTime inicio, LocalDateTime fim);
		
		//--- PESQUISA A SESSÃO ABERTA MAIS RECENTE --- 
		Optional<SessaoCaixa> findFirstByStatusOrderByDataAberturaDesc(StatusCaixa status);
		
		/**
		 * Lista todas as sessões atualmente abertas, em qualquer caixa físico.
		 *
		 * Uso: painel/dashboard do gerente — "quais caixas estão em operação agora".
		 * Não recebe parâmetro de Caixa porque a pergunta é sobre TODOS eles.
		 */
		Page<SessaoCaixa>findByStatus(StatusCaixa status, Pageable pageable);
		
		/**
		 * Verifica se UM caixa físico específico já possui uma sessão aberta.
		 *
		 * Por que boolean em vez de Optional<SessaoCaixa>?
		 * A Service só precisa de uma resposta sim/não pra decidir se bloqueia
		 * a abertura — não precisa dos dados da sessão (quem abriu, quando).
		 * Spring Data gera um SELECT EXISTS(...), mais barato que carregar a
		 * entidade inteira com seus relacionamentos só pra jogar fora depois.
		 */
		boolean existsByCaixaAndStatus(Caixa caixa, StatusCaixa status);
		
		/**
		 * Verifica se UM operador já tem caixa físico aberto.
		 */
		boolean existsByOperadorAndStatus(Usuario operador, StatusCaixa status);

}
