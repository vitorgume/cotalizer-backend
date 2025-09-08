package com.gumeinteligenciacomercial.orcaja.infrastructure.security.securityConfig;

import com.gumeinteligenciacomercial.orcaja.application.usecase.google.CustomOAuth2UserUseCase;
import com.gumeinteligenciacomercial.orcaja.application.usecase.google.CustomOidUserUseCase;
import com.gumeinteligenciacomercial.orcaja.application.usecase.google.GoogleOAuth2SuccessHandler;
import com.gumeinteligenciacomercial.orcaja.infrastructure.security.SecurityConfig;
import com.gumeinteligenciacomercial.orcaja.infrastructure.security.jwt.JwtAuthFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willAnswer;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TestController.class)
@Import(SecurityConfig.class)
class SecurityIntegrationConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;

    @MockitoBean
    private GoogleOAuth2SuccessHandler googleOAuth2SuccessHandler;

    @MockitoBean
    private CustomOAuth2UserUseCase customOAuth2UserUseCase;

    // << NOVO: requerido pelo método oauth2SecurityFilterChain(...)
    @MockitoBean
    private CustomOidUserUseCase customOidUserUseCase;

    @BeforeEach
    void setup() throws Exception {
        // Deixa o JwtAuthFilter transparente no teste
        willAnswer(invocation -> {
            ServletRequest req = invocation.getArgument(0);
            ServletResponse res = invocation.getArgument(1);
            FilterChain chain = invocation.getArgument(2);
            chain.doFilter(req, res);
            return null;
        }).given(jwtAuthFilter)
                .doFilter(any(ServletRequest.class), any(ServletResponse.class), any(FilterChain.class));
    }

    @Test
    void endpointsPublicosDevemEstarAcessiveis() throws Exception {
        mockMvc.perform(get("/login").accept(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())
                .andExpect(content().string("Login"));

        mockMvc.perform(get("/usuarios/cadastro").accept(MediaType.TEXT_PLAIN))
                .andExpect(status().is4xxClientError())
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRetornaUnauthorizedSemAutenticacao() throws Exception {
        mockMvc.perform(get("/api/hello").accept(MediaType.TEXT_PLAIN))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Unauthorized"));
    }

    @Test
    void deveRetornaroOkComAutenticacao() throws Exception {
        mockMvc.perform(get("/api/hello").with(user("user")).accept(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello API"));
    }

    @Test
    void deveEstarAcessiveisEndpointsOAuth2() throws Exception {
        mockMvc.perform(get("/oauth2/test").accept(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())
                .andExpect(content().string("OAuth2"));
    }
}