package com.flowpay.atendimento.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

/**
 * Configuração centralizada de CORS.
 * Permite requisições do frontend Angular (localhost:4200).
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // Permite credenciais (cookies, headers de autenticação)
        config.setAllowCredentials(true);

        // Permite origens específicas
        config.setAllowedOriginPatterns(Arrays.asList(
                "http://localhost:*",      // Desenvolvimento local (qualquer porta)
                "http://127.0.0.1:*",      // Alternativa localhost
                "https://*.flowpay.com.br" // Produção (exemplo)
        ));

        // Permite todos os headers
        config.addAllowedHeader("*");

        // Permite todos os métodos HTTP
        config.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"
        ));

        // Expõe headers customizados para o cliente
        config.setExposedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "X-Total-Count"
        ));

        // Cache da configuração CORS por 1 hora
        config.setMaxAge(3600L);

        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
