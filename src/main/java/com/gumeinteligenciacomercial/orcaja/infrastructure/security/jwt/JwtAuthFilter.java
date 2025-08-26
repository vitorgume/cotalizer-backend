package com.gumeinteligenciacomercial.orcaja.infrastructure.security.jwt;

import com.gumeinteligenciacomercial.orcaja.application.gateway.AuthTokenGateway;
import com.gumeinteligenciacomercial.orcaja.infrastructure.exceptions.TokenInvalidoException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final AuthTokenGateway tokenGateway;
    private final AntPathMatcher matcher = new AntPathMatcher();

    private static final List<String> PUBLIC_PATTERNS = List.of(
            "/auth/login",
            "/auth/refresh",
            "/auth/logout",
            "/auth/google/success",
            "/usuarios/cadastro",
            "/usuarios/alterar/senha",
            "/senhas/solicitar/nova",
            "/verificaoes/email/**",
            "/arquivos/**",
            "/oauth2/**",
            "/login/oauth2/**"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {

        final String path = request.getRequestURI();

        if ("OPTIONS".equalsIgnoreCase(request.getMethod()) || isPublic(path)) {
            chain.doFilter(request, response);
            return;
        }

        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            chain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7);
        try {
            var parsed = tokenGateway.parse(jwt);

            if (!"access".equals(parsed.type())) {
                chain.doFilter(request, response);
                return;
            }

            List<GrantedAuthority> authorities = parsed.roles() == null
                    ? List.of()
                    : parsed.roles().stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            var authentication = new UsernamePasswordAuthenticationToken(
                    parsed.subjectEmail(), null, authorities
            );
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (JwtException | IllegalArgumentException e) {
            throw new TokenInvalidoException();
        }

        chain.doFilter(request, response);
    }

    private boolean isPublic(String path) {
        for (String pattern : PUBLIC_PATTERNS) {
            if (matcher.match(pattern, path)) return true;
        }
        return false;
    }
}
