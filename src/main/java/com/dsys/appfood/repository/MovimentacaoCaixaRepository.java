package com.dsys.appfood.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dsys.appfood.domain.enums.TipoMovimentacao;
import com.dsys.appfood.domain.model.MovimentacaoCaixa;
import com.dsys.appfood.dto.ResumoCaixaResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Repository
public interface MovimentacaoCaixaRepository extends JpaRepository<MovimentacaoCaixa, Integer>{
	
	
	// --- PESQUISAR MOVIMENTAÇÃO POR DATA  ---
	List<MovimentacaoCaixa> findByDataHoraMovimentoBetween(LocalDateTime dataInicio, LocalDateTime dataFim);
	
	// --- CONSULTAR MOVIMENTAÇÃO DE UM CAIXA ---
	List<MovimentacaoCaixa> findByCaixaId(Integer caixaId);
	
	// --- PESQUISAR MOVIMENTAÇÃO DE UM CAIXA ORDENADO POR DATA  ---
	List<MovimentacaoCaixa>findByCaixaIdOrderByDataHoraMovimentoDesc(Integer caixaId);
	

	// --- PESQUISA MOVIMENTAÇÃO DE UM CAIXA DENTRO DE UM PERIODO ESPECIFICO ---
	List<MovimentacaoCaixa> findByCaixaIdAndDataHoraMovimentoBetweenOrderByDataHoraMovimentoDesc(Integer caixaId, LocalDateTime inicio, LocalDateTime fim);
	
	// --- CALCULA O TOTAL DE ENTRADAS DE UM CAIXA  ---
	@Query("SELECT COALESCE(SUM(m.valor),0) FROM MovimentacaoCaixa m WHERE m.caixa.id = :caixaId AND m.tipo = 'ENTRADA'")
	BigDecimal totalEntradas(@Param("caixaId") Integer caixaId);
	
	Optional<MovimentacaoCaixa> findByCaixaIdAndPedidoIdAndTipo(Integer caixaId, Integer pedidoId, TipoMovimentacao tipo);
	
	// --- CALCULA O TOTAL DE SAÍDAS DE UM CAIXA  ---
	@Query("SELECT COALESCE(SUM(m.valor),0) FROM MovimentacaoCaixa m WHERE m.caixa.id = :caixaId AND m.tipo = 'SAIDA'")
	BigDecimal totalSaidas(@Param("caixaId") Integer caixaId);
	
	/**
	 * Realiza uma projeção direta do Banco de Dados para a classe ResumoCaixaResponse.
	 * 
	 * CONCEITOS CHAVE:
	 * - 'SELECT new ...': Instancia o objeto Java ResumoCaixaResponse diretamente na query.
	 * - 'CASE WHEN': Funciona como um "if/else" dentro do SQL para separar os valores.
	 * - 'COALESCE(..., 0)': Garante que se a soma for nula (sem registros), o resultado seja 0.
	 * - 'GROUP BY': Agrupa os registros por caixa para permitir as funções de soma (SUM).
	 * 
	 * Este método é o mais "inteligente", pois ele já cria o objeto DTO e calcula tudo de uma vez no banco.
	 */ 
	@Query("SELECT new com.dsys.appfood.dto.ResumoCaixaResponse(" +
		       "m.caixa.id, " +
		       "COALESCE(SUM(CASE WHEN m.tipo = 'ENTRADA' THEN m.valor ELSE 0 END), 0), " +
		       "COALESCE(SUM(CASE WHEN m.tipo = 'SAIDA' THEN m.valor ELSE 0 END), 0), " +
		       "COALESCE(SUM(CASE WHEN m.tipo = 'ENTRADA' THEN m.valor ELSE -m.valor END), 0)) " +
		       "FROM MovimentacaoCaixa m " +
		       "WHERE m.caixa.id = :caixaId " +
		       "GROUP BY m.caixa.id")
	ResumoCaixaResponse resumoRapido(@Param("caixaId") Integer caixaId);
	
	/**
	 * Calcula a soma de todas as ENTRADAS em um intervalo de tempo específico.
	 * 
	 * CONCEITOS CHAVE:
	 * - 'BETWEEN': Filtra registros entre duas datas/horas (início e fim).
	 * - 'COALESCE': Evita que o Java receba "null" caso não haja entradas no período.
	 */
	@Query("SELECT COALESCE(SUM(m.valor), 0) FROM MovimentacaoCaixa m " +
	           "WHERE m.caixa.id = :caixaId " +
	           "AND m.tipo = 'ENTRADA' " +
	           "AND m.dataHoraMovimento BETWEEN :inicio AND :fim")
	    BigDecimal totalEntradasPeriodo(@Param("caixaId") Integer caixaId, 
	                                    @Param("inicio") LocalDateTime inicio, 
	                                    @Param("fim") LocalDateTime fim);

	/**
	 * Calcula a soma de todas as SAÍDAS em um intervalo de tempo específico.
	 * 
	 * CONCEITOS CHAVE:
	 * - 'm.tipo = 'SAIDA'': Filtra apenas movimentações que diminuem o caixa.
	 * - 'BigDecimal': Tipo de retorno ideal para garantir precisão centesimal (centavos).
	 */
	    @Query("SELECT COALESCE(SUM(m.valor), 0) FROM MovimentacaoCaixa m " +
	           "WHERE m.caixa.id = :caixaId " +
	           "AND m.tipo = 'SAIDA' " +
	           "AND m.dataHoraMovimento BETWEEN :inicio AND :fim")
	    BigDecimal totalSaidasPeriodo(@Param("caixaId") Integer caixaId, 
	                                  @Param("inicio") LocalDateTime inicio, 
	                                  @Param("fim") LocalDateTime fim);
}


