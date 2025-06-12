package com.gumeinteligenciacomercial.orcaja.infrastructure.security;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CspHeaderFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse res = (HttpServletResponse) servletResponse;
        res.setHeader("Content-Security-Policy", "default-src 'self'; script-src 'self';");
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
