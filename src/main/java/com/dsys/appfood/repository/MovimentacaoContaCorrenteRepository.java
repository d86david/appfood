package com.dsys.appfood.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dsys.appfood.domain.model.MovimentacaoContaCorrente;
import com.dsys.appfood.dto.ResumoContaCorrenteResponse;

@Repository
public interface MovimentacaoContaCorrenteRepository extends JpaRepository<MovimentacaoContaCorrente, Integer> {

	// --- PESQUISAR MOVIMENTAÇÃO DE UMA CONTA ORDENADO POR DATA --
	List<MovimentacaoContaCorrente> findByContaIdOrderByDataHoraDesc(Integer contaId);

	// --- PESQUISAR MOVIMENTAÇÃO DE UMA CONTA ORDENADA POR DATA ---
	List<MovimentacaoContaCorrente> findByContaIdAndDataHoraBetweenOrderByDataHoraDesc(Integer contaId,
			LocalDateTime inicio, LocalDateTime fim);

	// --- CALCULA O TOTAL DE ENTRADAS DE UMA CONTA ---
	@Query("SELECT COALESCE(SUM(m.valor),0) FROM MovimentacaoContaCorrente m WHERE m.conta.id = :contaId AND m.tipo = 'ENTRADA'")
	BigDecimal totalEntradasConta(@Param("contaId") Integer contaId);

	// --- CALCULA O TOTAL DE SAÍDAS DE UMA CONTA ---
	@Query("SELECT COALESCE(SUM(m.valor),0) FROM MovimentacaoContaCorrente m WHERE m.conta.id = :contaId AND m.tipo = 'SAIDA'")
	BigDecimal totalSaidasConta(@Param("contaId") Integer contaId);

	/**
	 * Realiza uma projeção direta do Banco de Dados para a classe
	 * ResumoContaCorrenteResponse.
	 * 
	 * CONCEITOS CHAVE: - 'SELECT new ...': Instancia o objeto Java
	 * ResumoContaCorrenteResponse diretamente na query. - 'CASE WHEN': Funciona como
	 * um "if/else" dentro do SQL para separar os valores. - 'COALESCE(..., 0)':
	 * Garante que se a soma for nula (sem registros), o resultado seja 0. - 'GROUP
	 * BY': Agrupa os registros por caixa para permitir as funções de soma (SUM).
	 * 
	 * Este método é o mais "inteligente", pois ele já cria o objeto DTO e calcula
	 * tudo de uma vez no banco.
	 */
	@Query("SELECT new com.dsys.appfood.dto.ResumoContaCorrenteResponse(" + "m.conta.id, "
			+ "COALESCE(SUM(CASE WHEN m.tipo = 'ENTRADA' THEN m.valor ELSE 0 END), 0), "
			+ "COALESCE(SUM(CASE WHEN m.tipo = 'SAIDA' THEN m.valor ELSE 0 END), 0), "
			+ "COALESCE(SUM(CASE WHEN m.tipo = 'ENTRADA' THEN m.valor ELSE -m.valor END), 0)) "
			+ "FROM MovimentacaoContaCorrente m " + "WHERE m.conta.id = :contaId " + "GROUP By m.conta.id")
	ResumoContaCorrenteResponse resumoRapido(@Param("contaId") Integer contaId);


/**
 * Calcula a soma de todas as ENTRADAS em um intervalo de tempo específico.
 * 
 * CONCEITOS CHAVE:
 * - 'BETWEEN': Filtra registros entre duas datas/horas (início e fim).
 * - 'COALESCE': Evita que o Java receba "null" caso não haja entradas no período.
 */
@Query("SELECT COALESCE(SUM(m.valor), 0) FROM MovimentacaoContaCorrente m " +
           "WHERE m.conta.id = :contaId " +
           "AND m.tipo = 'ENTRADA' " +
           "AND m.dataHora BETWEEN :inicio AND :fim")
    BigDecimal totalEntradasPeriodo(@Param("contaId") Integer contaId, 
                                    @Param("inicio") LocalDateTime inicio, 
                                    @Param("fim") LocalDateTime fim);

/**
 * Calcula a soma de todas as SAÍDAS em um intervalo de tempo específico.
 * 
 * CONCEITOS CHAVE:
 * - 'm.tipo = 'SAIDA'': Filtra apenas movimentações que diminuem o valor da conta.
 * - 'BigDecimal': Tipo de retorno ideal para garantir precisão centesimal (centavos).
 */
    @Query("SELECT COALESCE(SUM(m.valor), 0) FROM MovimentacaoContaCorrente m " +
           "WHERE m.conta.id = :contaId " +
           "AND m.tipo = 'SAIDA' " +
           "AND m.dataHora BETWEEN :inicio AND :fim")
    BigDecimal totalSaidasPeriodo(@Param("contaId") Integer contaId, 
                                  @Param("inicio") LocalDateTime inicio, 
                                  @Param("fim") LocalDateTime fim);

}