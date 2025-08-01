package com.gumeinteligenciacomercial.orcaja.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CspHeaderFilterTest {
    @InjectMocks
    private CspHeaderFilter filter;

    @Mock
    private FilterChain filterChain;

    @Mock
    private ServletRequest servletRequest;

    @Mock
    private ServletResponse servletResponse; // will mock as HttpServletResponse

    private HttpServletResponse httpResponse;

    @BeforeEach
    void setUp() {
        // servletResponse must be castable to HttpServletResponse
        httpResponse = mock(HttpServletResponse.class);
        // configure servletResponse mock to be the same instance
        servletResponse = httpResponse;
    }

    @Test
    void deveEscolherCspHeaderEInvoarChain() throws IOException, ServletException {
        filter.doFilter(servletRequest, servletResponse, filterChain);
        verify(httpResponse).setHeader("Content-Security-Policy", "default-src 'self'; script-src 'self';");
        verify(filterChain).doFilter(servletRequest, servletResponse);
    }

    @Test
    void deveLancarExceptionQuandoChainFalhaIOException() throws IOException, ServletException {
        doThrow(new IOException("chain broken")).when(filterChain)
                .doFilter(servletRequest, servletResponse);
        assertThrows(IOException.class, () ->
                filter.doFilter(servletRequest, servletResponse, filterChain)
        );
        verify(httpResponse).setHeader("Content-Security-Policy", "default-src 'self'; script-src 'self';");
    }

    @Test
    void deveLanvarExceptionQunadoChainFalhaServletException() throws IOException, ServletException {
        doThrow(new ServletException("chain error")).when(filterChain)
                .doFilter(servletRequest, servletResponse);

        assertThrows(ServletException.class, () ->
                filter.doFilter(servletRequest, servletResponse, filterChain)
        );

        verify(httpResponse).setHeader("Content-Security-Policy", "default-src 'self'; script-src 'self';");
    }

    @Test
    void deveNaoChamarChainSeResponseNaoForHtto() throws IOException, ServletException {
        ServletResponse nonHttp = mock(ServletResponse.class);

        filter.doFilter(servletRequest, nonHttp, filterChain);

        verifyNoInteractions(filterChain);
    }
}