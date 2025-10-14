package com.gustavo.finansync.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;
import java.util.Base64;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Endpoints públicos
                        .requestMatchers(
                                "/health/**",
                                "/auth/**",
                                "/transactions/**",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()
                        // Todas as outras requisições exigem autenticação
                        .anyRequest().authenticated()
                );
                // Habilita a validação de tokens JWT
                //http.oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
                http.oauth2ResourceServer(oauth2 -> oauth2.jwt());

        return http.build();
    }

    // Seus outros Beans (passwordEncoder, corsConfigurationSource) continuam iguais...
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /*@Bean
    public JwtDecoder jwtDecoder() {
        SecretKeySpec secretKey = new SecretKeySpec("${jwtSecret}".getBytes(), "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(secretKey).build();
    }*/

    @Bean
    public JwtDecoder jwtDecoder() {
        // **A CORREÇÃO ESTÁ AQUI**
        // 1. Decodifica a string Base64 para obter os bytes da chave
        byte[] keyBytes = Base64.getDecoder().decode(this.jwtSecret);

        // 2. Cria a SecretKeySpec com os bytes decodificados
        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "HmacSHA256");

        return NimbusJwtDecoder.withSecretKey(secretKey).build();
    }
}
