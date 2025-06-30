package com.gumeinteligenciacomercial.orcaja.infrastructure.security;

import com.gumeinteligenciacomercial.orcaja.infrastructure.security.jwt.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configure(http))
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)) // <- necessário para OAuth2 funcionar
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/usuarios/cadastro", "/oauth2/**", "/arquivos/acessar/**", "/arquivos/download/**", "/verificaoes/email").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth -> oauth
                        .defaultSuccessUrl("/auth/google/success", true)
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

}
