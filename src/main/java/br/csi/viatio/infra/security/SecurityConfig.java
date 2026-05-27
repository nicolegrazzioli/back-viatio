package br.csi.viatio.infra.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;

// Classe de configuração para ativar as regras de segurança do Spring Security
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AutenticacaoFilter autenticacaoFilter;
    
    public SecurityConfig(AutenticacaoFilter autenticacaoFilter) {
        this.autenticacaoFilter = autenticacaoFilter;
    }

    // Configuração principal das regras de segurança e rotas do sistema
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                // Desabilita a proteção contra CSRF porque a API é Stateless (usa Tokens JWT)
                .csrf(csrf -> csrf.disable())
                // Habilita as regras de CORS definidas abaixo
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // Configura a sessão como sem estado (Stateless), o servidor não mantém dados do usuário logado na memória
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Rotas públicas que qualquer usuário pode acessar (Registro de conta e Login)
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/register").permitAll()
                        
                        // Libera as rotas de documentação da API do Swagger (caso configurado)
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()

                        // Qualquer outra rota do sistema exige que o usuário esteja logado com um token JWT válido
                        .anyRequest().authenticated()
                )
                // Adiciona o filtro personalizado de validação de token (autenticacaoFilter) antes do filtro padrão do Spring
                .addFilterBefore(this.autenticacaoFilter, UsernamePasswordAuthenticationFilter.class)
                .build(); 
    }

    // Define quais origens, cabeçalhos e métodos HTTP são aceitos pela API (liberação para o Flutter conectar)
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Permite requisições vindas de qualquer IP/origem
        configuration.setAllowedOriginPatterns(Arrays.asList("*")); 
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Aplica essas regras para todos os caminhos (endpoints) da API
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    // Bean do gerenciador de autenticação utilizado no fluxo de Login
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    // Bean que define o codificador de senhas (BCrypt), usado para salvar senhas de forma segura com hash criptográfico
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

