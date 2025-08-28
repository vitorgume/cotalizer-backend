package com.gumeinteligenciacomercial.orcaja.application.usecase.google;

import com.gumeinteligenciacomercial.orcaja.application.gateway.AuthTokenGateway;
import com.gumeinteligenciacomercial.orcaja.application.usecase.UsuarioUseCase;
import com.gumeinteligenciacomercial.orcaja.domain.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.io.IOException;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoogleOAuth2SuccessHandlerTest {

    @Mock
    private AuthTokenGateway tokenService;

    @Mock
    private UsuarioUseCase usuarioUseCase;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Authentication authentication;

    @Mock
    private OAuth2User oAuth2User;

    @InjectMocks
    private GoogleOAuth2SuccessHandler handler;

    @Captor
    private ArgumentCaptor<String> redirectCaptor;

    @Test
    void onAuthenticationSuccessQuandoUsuarioTemCnpjRedirecionaParaMenu() throws IOException, ServletException {
        String email = "maria@exemplo.com";
        String token = "jwt-token-123";
        Usuario usuario = Usuario.builder()
                .id(UUID.randomUUID().toString())
                .email(email)
                .nome("Maria")
                .cnpj("00.000.000/0001-91")
                .build();

        when(authentication.getPrincipal()).thenReturn(oAuth2User);
        when(oAuth2User.getAttribute("email")).thenReturn(email);
        when(usuarioUseCase.consultarPorEmail(email)).thenReturn(usuario);
        when(tokenService.generateAccessToken(email, usuario.getId(), null)).thenReturn(token);

        handler.onAuthenticationSuccess(request, response, authentication);

        verify(usuarioUseCase, times(1)).consultarPorEmail(email);
        verify(tokenService, times(1)).generateAccessToken(email, usuario.getId(), null);
        verify(response, times(1)).sendRedirect(redirectCaptor.capture());

        String sentUrl = redirectCaptor.getValue();
        assert sentUrl.equals("http://localhost:5173/menu?token=" + token);
    }

    @Test
    void onAuthenticationSuccessQuandoUsuarioSemDocumentoRedirecionaParaSucesso() throws IOException, ServletException {
        String email = "joao@exemplo.com";
        String token = "jwt-token-456";
        Usuario usuario = Usuario.builder()
                .id(UUID.randomUUID().toString())
                .email(email)
                .nome("João")
                .build();

        when(authentication.getPrincipal()).thenReturn(oAuth2User);
        when(oAuth2User.getAttribute("email")).thenReturn(email);
        when(usuarioUseCase.consultarPorEmail(email)).thenReturn(usuario);
        when(tokenService.generateAccessToken(email, usuario.getId(), null)).thenReturn(token);

        handler.onAuthenticationSuccess(request, response, authentication);

        verify(usuarioUseCase, times(1)).consultarPorEmail(email);
        verify(tokenService, times(1)).generateAccessToken(email, usuario.getId(), null);
        verify(response, times(1)).sendRedirect(redirectCaptor.capture());

        String sentUrl = redirectCaptor.getValue();
        assert sentUrl.equals("http://localhost:5173/login/sucesso?token=" + token);
    }
}