package com.gumeinteligenciacomercial.orcaja.infrastructure.security;

import com.gumeinteligenciacomercial.orcaja.application.usecase.google.GoogleOAuth2SuccessHandler;
import com.gumeinteligenciacomercial.orcaja.infrastructure.security.jwt.JwtAuthFilter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@Profile("!test")
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final GoogleOAuth2SuccessHandler googleOAuth2SuccessHandler;


    @Bean
    @Order(0)
    public SecurityFilterChain filesChain(
            HttpSecurity http,
            @Qualifier("corsConfigurationSource") CorsConfigurationSource cors,
            @Value("${app.fileschain-frontend-url}") String filesChainFrontendUrl
    ) throws Exception {

        http
                .securityMatcher("/arquivos/**")
                .csrf(csrf -> csrf.disable())
                .cors(c -> c.configurationSource(cors))
                .headers(h -> h
                        .contentTypeOptions(Customizer.withDefaults())
                        .referrerPolicy(r -> r.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.SAME_ORIGIN))
                        .permissionsPolicyHeader(p -> p.policy("geolocation=(), microphone=(), camera=()"))
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)
                        .contentSecurityPolicy(csp -> csp.policyDirectives(
                                "default-src 'self'; img-src 'self' data: https:; media-src 'self' https:; object-src 'none'; " +
                                        "frame-ancestors 'self' " + filesChainFrontendUrl
                        ))
                )
                .authorizeHttpRequests(a -> a
                        .requestMatchers(org.springframework.web.cors.CorsUtils::isPreFlightRequest).permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/arquivos/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/arquivos/**").permitAll()
                        .requestMatchers(HttpMethod.HEAD, "/arquivos/**").permitAll()
                        .anyRequest().denyAll()
                );

        return http.build();
    }

    @Bean
    @Order(1)
    public SecurityFilterChain apiSecurityFilterChain(
            HttpSecurity http,
            @Qualifier("corsConfigurationSource") CorsConfigurationSource cors,
            CookieCsrfTokenRepository csrfRepo
    ) throws Exception {

        var handler = new CsrfTokenRequestAttributeHandler();
        handler.setCsrfRequestAttributeName("_csrf");

        http
                .securityMatcher("/api/**", "/usuarios/**", "/login", "/auth/**", "/verificaoes/**", "/orcamentos/**", "/csrf", "/assinaturas/**")
                .csrf(csrf -> csrf
                        .csrfTokenRepository(csrfRepo)
                        .csrfTokenRequestHandler(handler)
                        .ignoringRequestMatchers(
                                org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher.withDefaults().matcher("/oauth2/**"),
                                org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher.withDefaults().matcher("/arquivos/**"),
                                org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.POST, "/auth/refresh"),
                                org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.POST, "/auth/logout")
                        )
                )
                .cors(c -> c.configurationSource(cors))
                .headers(h -> h
                        .frameOptions(f -> f.sameOrigin())
                        .contentTypeOptions(Customizer.withDefaults())
                        .referrerPolicy(r -> r.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.SAME_ORIGIN))
                        .permissionsPolicyHeader(p -> p.policy("geolocation=(), microphone=(), camera=()"))
                )
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                        .requestMatchers(HttpMethod.GET, "/csrf").permitAll()
                        .requestMatchers(
                                "/login",
                                "/auth/login",
                                "/auth/refresh",
                                "/auth/logout",
                                "/auth/google/success",
                                "/usuarios/cadastro",
                                "/verificaoes/email/**",
                                "/usuarios/alterar/senha",
                                "/senhas/solicitar/nova"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(ex -> ex.authenticationEntryPoint((req, res, e) -> {
                    res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    res.getWriter().write("Unauthorized");
                }));

        http.addFilterAfter(new CsrfCookieTouchFilter(csrfRepo), org.springframework.security.web.csrf.CsrfFilter.class);

        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain oauth2SecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/oauth2/**", "/login/oauth2/**")
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .oauth2Login(oauth -> oauth.successHandler(googleOAuth2SuccessHandler))
                .csrf(csrf -> csrf.disable());
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource(
            @Value("${app.cors.allowed-origins}") List<String> origins
    ) {
        var cfg = new CorsConfiguration();
        cfg.setAllowedOrigins(origins);
        cfg.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS", "HEAD"));
        cfg.setAllowedHeaders(List.of("*"));
        cfg.setAllowCredentials(true);
        cfg.setMaxAge(3600L);

        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }

    @Bean
    public CookieCsrfTokenRepository csrfTokenRepository(
            @Value("${app.security.csrf.secure}") boolean secure,
            @Value("${app.security.csrf.sameSite}") String sameSite
    ) {
        var repo = CookieCsrfTokenRepository.withHttpOnlyFalse();
        repo.setCookieCustomizer(c -> c
                .path("/")
                .httpOnly(false)
                .secure(secure)
                .sameSite(sameSite)
        );
        return repo;
    }
}

class CsrfCookieTouchFilter extends OncePerRequestFilter {
    private final CookieCsrfTokenRepository repo;

    CsrfCookieTouchFilter(CookieCsrfTokenRepository repo) {
        this.repo = repo;
    }

    @Override
    protected void doFilterInternal(
            jakarta.servlet.http.HttpServletRequest request,
            jakarta.servlet.http.HttpServletResponse response,
            jakarta.servlet.FilterChain filterChain
    ) throws java.io.IOException, jakarta.servlet.ServletException {

        try {
            CsrfToken token = (CsrfToken) request.getAttribute("_csrf");
            if (token == null) {
                token = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
            }

            if (token == null) {
                token = repo.loadToken(request);
                if (token == null) {
                    token = repo.generateToken(request);
                }

                request.setAttribute(CsrfToken.class.getName(), token);
                request.setAttribute("_csrf", token);
            }

            repo.saveToken(token, request, response);

            filterChain.doFilter(request, response);
        } catch (Exception ex) {
            logger.error("Erro ao filtar requisição.", ex);
        }

    }
}

