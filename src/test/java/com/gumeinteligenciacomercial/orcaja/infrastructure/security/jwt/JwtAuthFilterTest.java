package com.gumeinteligenciacomercial.orcaja.infrastructure.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private JwtAuthFilter filter;

    @BeforeEach
    void setup() {
        filter = new JwtAuthFilter(jwtUtil);
        SecurityContextHolder.clearContext();
    }

    @Test
    void rotasPublicasDeveSeguirSemValidarToken() throws ServletException, IOException {
        List<String> publicas = List.of(
                "/login",
                "/usuarios/cadastro",
                "/oauth2/",
                "/arquivos/acessar/",
                "/arquivos/download/",
                "/verificaoes/email",
                "/senhas/solicitar/nova",
                "/usuarios/alterar/senha"
        );

        for (String rota : publicas) {
            reset(filterChain);
            when(request.getRequestURI()).thenReturn(rota);

            filter.doFilterInternal(request, response, filterChain);

            // deve chamar exatamente uma vez, sem mexer no SecurityContext
            verify(filterChain, times(1)).doFilter(request, response);
            assertNull(SecurityContextHolder.getContext().getAuthentication());
        }
    }

    @Test
    void quandoNaoTiverHeaderAuthorizationDeveSeguirSemAutenticacao() throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/api/protegido");
        when(request.getHeader("Authorization")).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void quandoHeaderNaoComecarComBearerDeveSeguirSemAutenticacao() throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/api/protegido");
        when(request.getHeader("Authorization")).thenReturn("Token abc.def.ghi");

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void quandoTokenInvalidoDeveSeguirSemAutenticacao() throws ServletException, IOException {
        String fakeJwt = "Bearer tok.inval.id";
        when(request.getRequestURI()).thenReturn("/api/protegido");
        when(request.getHeader("Authorization")).thenReturn(fakeJwt);

        when(jwtUtil.extractUsername("tok.inval.id")).thenReturn("user@example.com");
        when(jwtUtil.isTokenValid("tok.inval.id")).thenReturn(false);

        filter.doFilterInternal(request, response, filterChain);

        verify(jwtUtil).extractUsername("tok.inval.id");
        verify(jwtUtil).isTokenValid("tok.inval.id");
        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void quandoTokenValidoDevePreencherSecurityContext() throws ServletException, IOException {
        String fakeJwt = "Bearer abc.def.ghi";
        when(request.getRequestURI()).thenReturn("/api/protegido");
        when(request.getHeader("Authorization")).thenReturn(fakeJwt);

        when(jwtUtil.extractUsername("abc.def.ghi")).thenReturn("user@example.com");
        when(jwtUtil.isTokenValid("abc.def.ghi")).thenReturn(true);

        filter.doFilterInternal(request, response, filterChain);

        verify(jwtUtil).extractUsername("abc.def.ghi");
        verify(jwtUtil).isTokenValid("abc.def.ghi");
        verify(filterChain).doFilter(request, response);

        var auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth, "Deve ter setado Authentication no SecurityContext");
        assertInstanceOf(UsernamePasswordAuthenticationToken.class, auth);
        assertEquals("user@example.com", auth.getPrincipal());
        assertNull(auth.getCredentials());
        assertTrue(auth.getAuthorities().isEmpty());
    }

    @Test
    void quandoExtractUsernameRetornarNullDeveSeguirSemAutenticacao() throws ServletException, IOException {
        String fakeJwt = "Bearer xyz.abc.123";
        when(request.getRequestURI()).thenReturn("/api/protegido");
        when(request.getHeader("Authorization")).thenReturn(fakeJwt);

        when(jwtUtil.extractUsername("xyz.abc.123")).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        verify(jwtUtil).extractUsername("xyz.abc.123");
        verify(jwtUtil, never()).isTokenValid(any());
        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}