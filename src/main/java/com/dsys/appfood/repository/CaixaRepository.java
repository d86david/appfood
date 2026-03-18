package com.dsys.appfood.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dsys.appfood.domain.enums.StatusCaixa;
import com.dsys.appfood.domain.model.Caixa;
import com.dsys.appfood.domain.model.Usuario;

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
	
	//--- PESQUISAR POR DATA ---
	List<Caixa> findByDataAberturaBetween(LocalDateTime inicio, LocalDateTime fim);
	
	//--- PESQUISAR STATUS DO CAIXA --- 
	Optional<Caixa> findFirstByStatusOrderByDataAberturaDesc(StatusCaixa status);
	
	//--- PESQUISA POR OPERADOR ---
	Optional<Caixa> findByOperadorAndStatus(Usuario operador, StatusCaixa status);


}
