package com.dsys.appfood.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Declaração do BCryptPasswordEncoder como um Bean do Spring.
 * 
 * Ao declarar aqui é possivel injetar o PasswordEncoder em qualuqer Service
 * sem criar instancia manualmente
 * 
 * Essa Classe está separada porque o encoder não é responsabilidade de nenhum 
 * Service específico - é uma ferramenta transversal do sistema. 
 */
@Configuration
public class SecurityConfig {
	
	/**
	 * Cria o encoder com fator de custo 12.
     * Fator 12 = ~200ms por hash = equilíbrio segurança/performance
     * 
     * Se não passar o fator, o padrão do Spring é 10.
     * Para sistemas críticos (financeiro), considere 13 ou 14.
	 */
	
	public PasswordEncoder passwordEncoder () {
		return new BCryptPasswordEncoder(12);
	}

}
