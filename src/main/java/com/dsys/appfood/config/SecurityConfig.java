package com.dsys.appfood.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

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
	@Bean
	public PasswordEncoder passwordEncoder () {
		return new BCryptPasswordEncoder(12);
	}
	
	/**
     * Desabilita a segurança de rotas durante o desenvolvimento.
     * Permite acessar todos os endpoints sem autenticação.
     *
     * ATENÇÃO: remover isso ou reconfigurar antes de ir para produção.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()
            );
        return http.build();
    }

}
