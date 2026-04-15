package com.dsys.appfood.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dsys.appfood.domain.enums.FormaPagamento;
import com.dsys.appfood.domain.model.ConfiguracaoPagamento;

public interface ConfiguracaoPagamentoRepository extends JpaRepository<ConfiguracaoPagamento, Integer> {
	
    //Localiza a configuração ativa para uma forma de pagamento específica.
    Optional<ConfiguracaoPagamento> findByFormaPagamento(FormaPagamento forma);

}
