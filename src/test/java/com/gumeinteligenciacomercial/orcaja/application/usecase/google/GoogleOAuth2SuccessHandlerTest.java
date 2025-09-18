package com.gumeinteligenciacomercial.orcaja.application.usecase.google;

import com.gumeinteligenciacomercial.orcaja.application.gateway.AuthTokenGateway;
import com.gumeinteligenciacomercial.orcaja.application.usecase.UsuarioUseCase;
import com.gumeinteligenciacomercial.orcaja.domain.TipoCadastro;
import com.gumeinteligenciacomercial.orcaja.domain.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
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

    private GoogleOAuth2SuccessHandler handler;

    private static final String MENU_URL = "http://localhost:5173/menu";
    private static final String LOGIN_URL = "http://localhost:5173/login/sucesso";
    private static final boolean SECURE = false;
    private static final String SAME_SITE = "Lax";

    @BeforeEach
    void setUp() {
        handler = new GoogleOAuth2SuccessHandler(
                tokenService,
                usuarioUseCase,
                MENU_URL,
                LOGIN_URL,
                SECURE,
                SAME_SITE
        );
    }

    @Captor
    private ArgumentCaptor<String> redirectCaptor;

    @Test
    void onAuthenticationSuccess_quandoUsuarioTemCnpj_redirecionaParaMenu_eSetaCookie() throws Exception {
        String email = "maria@exemplo.com";
        String refresh = "refresh-123";
        Usuario usuario = Usuario.builder()
                .id(UUID.randomUUID().toString())
                .email(email)
                .nome("Maria")
                .tipoCadastro(TipoCadastro.GOOGLE)
                .build();

        when(authentication.getPrincipal()).thenReturn(oAuth2User);
        when(oAuth2User.getAttribute("email")).thenReturn(email);
        when(usuarioUseCase.consultarPorEmail(email)).thenReturn(usuario);
        when(tokenService.generateRefreshToken(email, usuario.getId())).thenReturn(refresh);

        handler.onAuthenticationSuccess(request, response, authentication);

        verify(usuarioUseCase).consultarPorEmail(email);
        verify(tokenService).generateRefreshToken(email, usuario.getId());

        // Verifica cookie
        ArgumentCaptor<String> cookieCaptor = ArgumentCaptor.forClass(String.class);
        verify(response).addHeader(eq(org.springframework.http.HttpHeaders.SET_COOKIE), cookieCaptor.capture());
        String cookie = cookieCaptor.getValue();
        org.junit.jupiter.api.Assertions.assertTrue(cookie.contains("REFRESH_TOKEN=" + refresh));
        org.junit.jupiter.api.Assertions.assertTrue(cookie.contains("HttpOnly"));
        org.junit.jupiter.api.Assertions.assertTrue(cookie.contains("Path=/"));
        org.junit.jupiter.api.Assertions.assertTrue(cookie.contains("Max-Age=")); // 30 dias
        // Se SECURE=true, você pode checar "Secure"; como está false, não precisa

        // Verifica redirect
        verify(response).sendRedirect(redirectCaptor.capture());
        org.junit.jupiter.api.Assertions.assertEquals(MENU_URL, redirectCaptor.getValue());
    }

    @Test
    void onAuthenticationSuccess_quandoUsuarioSemDocumento_redirecionaParaLogin_eSetaCookie() throws Exception {
        String email = "joao@exemplo.com";
        String refresh = "refresh-456";
        Usuario usuario = Usuario.builder()
                .id(UUID.randomUUID().toString())
                .email(email)
                .nome("João")
                .tipoCadastro(TipoCadastro.TRADICIONAL)
                .build();

        when(authentication.getPrincipal()).thenReturn(oAuth2User);
        when(oAuth2User.getAttribute("email")).thenReturn(email);
        when(usuarioUseCase.consultarPorEmail(email)).thenReturn(usuario);
        when(tokenService.generateRefreshToken(email, usuario.getId())).thenReturn(refresh);

        handler.onAuthenticationSuccess(request, response, authentication);

        verify(usuarioUseCase).consultarPorEmail(email);
        verify(tokenService).generateRefreshToken(email, usuario.getId());

        ArgumentCaptor<String> cookieCaptor = ArgumentCaptor.forClass(String.class);
        verify(response).addHeader(eq(org.springframework.http.HttpHeaders.SET_COOKIE), cookieCaptor.capture());
        String cookie = cookieCaptor.getValue();
        org.junit.jupiter.api.Assertions.assertTrue(cookie.contains("REFRESH_TOKEN=" + refresh));

        verify(response).sendRedirect(redirectCaptor.capture());
        org.junit.jupiter.api.Assertions.assertEquals(LOGIN_URL, redirectCaptor.getValue());
    }

    @Test
    void onAuthenticationSuccess_quandoUsuarioTemApenasCpf_redirecionaParaMenu_eCookieContemSameSite() throws Exception {
        String email = "ana@exemplo.com";
        String refresh = "refresh-789";
        Usuario usuario = Usuario.builder()
                .id(UUID.randomUUID().toString())
                .email(email)
                .nome("Ana")
                .tipoCadastro(TipoCadastro.GOOGLE)
                .build();

        when(authentication.getPrincipal()).thenReturn(oAuth2User);
        when(oAuth2User.getAttribute("email")).thenReturn(email);
        when(usuarioUseCase.consultarPorEmail(email)).thenReturn(usuario);
        when(tokenService.generateRefreshToken(email, usuario.getId())).thenReturn(refresh);

        handler.onAuthenticationSuccess(request, response, authentication);

        ArgumentCaptor<String> cookieCaptor = ArgumentCaptor.forClass(String.class);
        verify(response).addHeader(eq(org.springframework.http.HttpHeaders.SET_COOKIE), cookieCaptor.capture());
        String cookie = cookieCaptor.getValue();
        org.junit.jupiter.api.Assertions.assertTrue(cookie.contains("REFRESH_TOKEN=" + refresh));
        org.junit.jupiter.api.Assertions.assertTrue(cookie.contains("HttpOnly"));
        org.junit.jupiter.api.Assertions.assertTrue(cookie.contains("SameSite=" + SAME_SITE));
        org.junit.jupiter.api.Assertions.assertFalse(cookie.contains("Secure"));

        verify(response).sendRedirect(redirectCaptor.capture());
        org.junit.jupiter.api.Assertions.assertEquals(MENU_URL, redirectCaptor.getValue());

        verify(usuarioUseCase).consultarPorEmail(email);
        verify(tokenService).generateRefreshToken(email, usuario.getId());
    }

}