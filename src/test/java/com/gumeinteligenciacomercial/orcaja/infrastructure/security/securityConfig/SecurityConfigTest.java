package com.gumeinteligenciacomercial.orcaja.infrastructure.security.securityConfig;

import com.gumeinteligenciacomercial.orcaja.application.usecase.google.GoogleOAuth2SuccessHandler;
import com.gumeinteligenciacomercial.orcaja.infrastructure.security.SecurityConfig;
import com.gumeinteligenciacomercial.orcaja.infrastructure.security.jwt.JwtAuthFilter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SecurityConfigTest {

    @Mock
    private JwtAuthFilter jwtAuthFilter;

    @Mock
    private GoogleOAuth2SuccessHandler googleOAuth2SuccessHandler;

    @Mock
    private AuthenticationConfiguration authConfig;

    @InjectMocks
    private SecurityConfig securityConfig;

    @Test
    void quandoGerenciadorDeAutenticacaoForChamadoRetorneGerenciadorConfigurado() throws Exception {
        AuthenticationManager manager = org.mockito.Mockito.mock(AuthenticationManager.class);
        when(authConfig.getAuthenticationManager()).thenReturn(manager);

        AuthenticationManager result = securityConfig.authenticationManager(authConfig);

        assertSame(manager, result, "authenticationManager should return the manager from config");
        verify(authConfig).getAuthenticationManager();
    }
}