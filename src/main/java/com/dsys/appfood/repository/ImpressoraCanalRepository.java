package com.dsys.appfood.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dsys.appfood.domain.model.ImpressoraCanal;

public interface ImpressoraCanalRepository extends JpaRepository<ImpressoraCanal, Integer>{
	
	/**
     * Busca o mapeamento ativo para um canal específico.
     * 
     * CONCEITO: Query com JOIN FETCH
     * Carrega a Impressora junto para evitar LazyInitializationException
     * quando o Service precisar acessar a largura da impressora.
     * 
     * @param nomeCanal Nome do canal (ex: "BALCAO", "PIZZAS")
     * @return Optional com o mapeamento, ou vazio se não houver
     */
	@Query("SELECT ic FROM ImpressoraCanal ic "+
			"JOIN FETCH ic.impressora " +
			"WHERE ic.canal.nome = :nomeCanal AND ic.ativo = true AND ic.impressora.ativa = true")
	Optional<ImpressoraCanal> findByCanalNomeAtivo(@Param("nomeCanal") String nomeCanal);
	
	/**
     * Lista todos os mapeamentos de uma impressora específica.
     * Útil para saber quais canais uma impressora recebe.
     */
    @Query("SELECT ic FROM ImpressoraCanal ic " +
           "JOIN FETCH ic.canal " +
           "WHERE ic.impressora.id = :impressoraId AND ic.ativo = true")
    List<ImpressoraCanal> findByImpressoraIdAtivo(@Param("impressoraId") Integer impressoraId);

    /**
     * Verifica se já existe um mapeamento entre uma impressora e um canal.
     * Usado para evitar duplicatas.
     */
    boolean existsByImpressoraIdAndCanalId(Integer impressoraId, Integer canalId);

}
