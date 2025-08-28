package com.gumeinteligenciacomercial.orcaja.infrastructure.security.jwt;

import com.gumeinteligenciacomercial.orcaja.application.gateway.AuthTokenGateway;
import com.gumeinteligenciacomercial.orcaja.infrastructure.exceptions.TokenInvalidoException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.JwtException;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthFilterTest {

    @Mock
    AuthTokenGateway tokenGateway;

    @InjectMocks
    JwtAuthFilter filter;

    @Mock
    HttpServletRequest request;
    @Mock
    HttpServletResponse response;
    @Mock
    FilterChain chain;

    @BeforeEach
    void setup() {
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void arr(String method, String path, String authHeader) {
        when(request.getMethod()).thenReturn(method);
        when(request.getRequestURI()).thenReturn(path);
        if (authHeader != null) {
            when(request.getHeader("Authorization")).thenReturn(authHeader);
        }
    }

    @Test
    void deveIgnorarOptions() throws ServletException, IOException {
        arr("OPTIONS", "/qualquer", null);

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        verifyNoInteractions(tokenGateway);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void deveIgnorarRotasPublicas_exato() throws Exception {
        arr("GET", "/auth/login", null);

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        verifyNoInteractions(tokenGateway);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void deveIgnorarRotasPublicas_wildcard() throws Exception {
        arr("GET", "/verificaoes/email/abc123", null); // bate no padrão "/verificaoes/email/**"

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        verifyNoInteractions(tokenGateway);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void jaAutenticado_noContexto_deveApenasSeguir() throws Exception {
        Authentication pre = new UsernamePasswordAuthenticationToken("pre", null, List.of());
        SecurityContextHolder.getContext().setAuthentication(pre);

        arr("GET", "/api/privada", null);

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        verifyNoInteractions(tokenGateway); // não reprocessa
        assertSame(pre, SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void semAuthorizationHeader_deveSeguir() throws Exception {
        arr("GET", "/api/privada", null);

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        verifyNoInteractions(tokenGateway);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void authorizationNaoBearer_deveSeguir() throws Exception {
        arr("GET", "/api/privada", "Basic abc123");

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        verifyNoInteractions(tokenGateway);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void accessValido_semRoles_deveAutenticarSemAuthorities() throws Exception {
        arr("GET", "/api/privada", "Bearer abc.def");

        when(tokenGateway.parse("abc.def"))
                .thenReturn(new AuthTokenGateway.ParsedToken("alice@example.com", "u-1", "access", null));

        filter.doFilter(request, response, chain);

        // parse chamado com o JWT correto (substring depois de "Bearer ")
        verify(tokenGateway).parse("abc.def");
        verify(chain).doFilter(request, response);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertEquals("alice@example.com", auth.getPrincipal());
        assertTrue(auth.getAuthorities().isEmpty());
    }

    @Test
    void accessValido_comRoles_deveAutenticarComAuthorities() throws Exception {
        arr("GET", "/api/privada", "Bearer token.jwt");

        when(tokenGateway.parse("token.jwt"))
                .thenReturn(new AuthTokenGateway.ParsedToken("bob@example.com", "u-2", "access", List.of("ADMIN", "USER")));

        filter.doFilter(request, response, chain);

        verify(tokenGateway).parse("token.jwt");
        verify(chain).doFilter(request, response);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertEquals("bob@example.com", auth.getPrincipal());
        assertTrue(auth.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN")));
        assertTrue(auth.getAuthorities().contains(new SimpleGrantedAuthority("USER")));
    }

    @Test
    void tokenTipoRefresh_naoDeveAutenticar() throws Exception {
        arr("GET", "/api/privada", "Bearer xyz.123");

        when(tokenGateway.parse("xyz.123"))
                .thenReturn(new AuthTokenGateway.ParsedToken("carol@example.com", "u-3", "refresh", List.of("USER")));

        filter.doFilter(request, response, chain);

        verify(tokenGateway).parse("xyz.123");
        verify(chain).doFilter(request, response);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void parseLancaJwtException_deveLancarTokenInvalidoException() throws Exception {
        arr("GET", "/api/privada", "Bearer bad.jwt");

        when(tokenGateway.parse("bad.jwt"))
                .thenThrow(new JwtException("invalid"));

        assertThrows(TokenInvalidoException.class,
                () -> filter.doFilter(request, response, chain));

        verify(tokenGateway).parse("bad.jwt");
        verify(chain, never()).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void parseLancaIllegalArgumentException_deveLancarTokenInvalidoException() throws Exception {
        arr("GET", "/api/privada", "Bearer bad2.jwt");

        when(tokenGateway.parse("bad2.jwt"))
                .thenThrow(new IllegalArgumentException("bad arg"));

        assertThrows(TokenInvalidoException.class,
                () -> filter.doFilter(request, response, chain));

        verify(tokenGateway).parse("bad2.jwt");
        verify(chain, never()).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}