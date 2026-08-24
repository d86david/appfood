package com.dsys.appfood.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dsys.appfood.domain.model.Caixa;

/**
 * Repository para a entidade Caixa que representa o caixa físico
 * 
 * CONCEITO: Repository Pattern
 * Abstrai o acesso ao banco de dados
 */
@Repository
public interface CaixaRepository extends JpaRepository<Caixa, Integer>{

	/*  O Spring Data JPA cria automáticamente os métodos:
	 *  - save()       -> Salvar
	 *  - findById(  ) -> buscar por ID
	 *  - findAll()    -> listar todos
	 *  - delete()     -> deletar
	 *  - deleteById() -> deletar por ID
	 */

	/*  RETORNOS DOS MÉTODOS
	 *  - List<Entidade> -> Usar quando a consulta pode retornar vários registros
	 *  - Optional<Entidade> -> Usar quando espera apenas um ou nenhum resultado. O optional é excelente
	 *      para evitar o erro NullPointerException.
	 *  - Entidade -> Usar quando tem certeza absoluta que o registro existe (mas o Optional é mais seguro)
	 *  - Page<Entiade> -> Usar para paginação (quando tem milhares de registros e quer mostrar de 10 em 10).
	 *
	 */

	//--- PESQUISAR CAIXA POR NOME  ---
	Optional<Caixa> findByNomeIgnoreCase(String nome);
	
	// LISTA APENAS CAIXAS ATIVOS ---
	List<Caixa> findByAtivoTrue();
	
	// LISTA CAIXAS POR LOCALIZAÇÃO ---
	List<Caixa> findByLocalizacaoIgnoreCase(String localizacao);


}
