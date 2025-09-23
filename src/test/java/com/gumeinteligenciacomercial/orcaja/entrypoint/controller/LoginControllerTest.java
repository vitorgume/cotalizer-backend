package com.gumeinteligenciacomercial.orcaja.entrypoint.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gumeinteligenciacomercial.orcaja.application.usecase.LoginUseCase;
import com.gumeinteligenciacomercial.orcaja.application.usecase.RefreshSessionUseCase;
import com.gumeinteligenciacomercial.orcaja.application.usecase.UsuarioUseCase;
import com.gumeinteligenciacomercial.orcaja.domain.AuthResult;
import com.gumeinteligenciacomercial.orcaja.domain.RefreshResult;
import com.gumeinteligenciacomercial.orcaja.domain.Usuario;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.LoginDto;
import com.gumeinteligenciacomercial.orcaja.entrypoint.mapper.LoginMapper;
import jakarta.servlet.http.Cookie;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Duration;
import java.util.Map;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(
        properties = {
                "spring.task.scheduling.enabled=false",
                "openia.api.key=TEST_OPENAI_KEY",
                "security.api.key=TEST_SIGNATURES_KEY",
                "secret.key=5a6bf2660e4a4fb7ec956e43959e4e6f826a9662a1f4578bcab89e3178770615",
                "cotalizer.email.avaliacao=EMAIL_TESTE",
                "app.storage.s3.bucket=s3_teste",
                "app.storage.s3.region=teste",
                "app.files.public-base-url=teste",
                "api.assinatura.url=teste",
                "cotalizer.url.alteracao-email=EMAIL_TESTE",
                "google.redirect.menu.url=teste",
                "google.redirect.login.url=teste",
                "app.security.csrf.secure=true",
                "app.security.csrf.sameSite=None",
                "app.id.prompt.ia.gerador-orcamento=teste",
                "app.id.prompt.ia.interpretador-prompt=teste"
        }
)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class LoginControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    LoginUseCase loginUseCase;

    @MockitoBean
    RefreshSessionUseCase refreshSessionUseCase;

    @MockitoBean
    UsuarioUseCase usuarioUseCase;

    @MockitoBean
    JavaMailSender javaMailSender;

    private static final String REFRESH_COOKIE = "REFRESH_TOKEN";

    @Test
    void login_deveRetornar200_SetRefreshCookie_eBodyComTokenEUsuarioId() throws Exception {
        var reqJson = objectMapper.writeValueAsString(
                Map.of("email", "alice@example.com", "senha", "123456")
        );

        var usuario = mock(Usuario.class);
        when(usuario.getId()).thenReturn("u-1");

        var authResult = mock(AuthResult.class);
        when(authResult.getUsuario()).thenReturn(usuario);
        when(authResult.getAccessToken()).thenReturn("ACCESS_123");
        when(authResult.getRefreshToken()).thenReturn("REFRESH_456");

        try (MockedStatic<LoginMapper> mocked = mockStatic(LoginMapper.class)) {
            mocked.when(() -> LoginMapper.paraDomain(any(LoginDto.class))).thenReturn(null);

            given(loginUseCase.autenticar(any())).willReturn(authResult);

            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(reqJson))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.dado.usuarioId").value("u-1"))
                    .andExpect(jsonPath("$.dado.token").value("ACCESS_123"))
                    .andExpect(header().string(HttpHeaders.SET_COOKIE,
                            Matchers.allOf(
                                    Matchers.containsString(REFRESH_COOKIE + "=REFRESH_456"),
                                    Matchers.containsString("HttpOnly"),
                                    Matchers.containsString("Secure"),
                                    Matchers.containsString("SameSite=None"),
                                    Matchers.containsString("Path=/"),
                                    Matchers.containsString("Max-Age=" + Duration.ofDays(30).toSeconds())
                            )));

            verify(loginUseCase).autenticar(any());
        }
    }

    @Test
    void refresh_semCookie_deveRetornar401() throws Exception {
        mockMvc.perform(post("/auth/refresh"))
                .andExpect(status().isUnauthorized());
        verifyNoInteractions(refreshSessionUseCase);
    }

    @Test
    void refresh_cookieVazio_deveRetornar401() throws Exception {
        mockMvc.perform(post("/auth/refresh").header(HttpHeaders.COOKIE, REFRESH_COOKIE + "="))
                .andExpect(status().isUnauthorized());
        verifyNoInteractions(refreshSessionUseCase);
    }

    @Test
    void refresh_ok_deveSetarNovoCookie_eRetornarNovoAccessToken() throws Exception {
        var oldRefresh = "OLD_RT";
        var newRefresh = "NEW_RT";
        var newAccess  = "NEW_AT";

        var rr = mock(RefreshResult.class);
        when(rr.getNewAccessToken()).thenReturn(newAccess);
        when(rr.getNewRefreshToken()).thenReturn(newRefresh);
        given(refreshSessionUseCase.renovar(oldRefresh)).willReturn(rr);

        mockMvc.perform(post("/auth/refresh")
                        .cookie(new jakarta.servlet.http.Cookie(REFRESH_COOKIE, oldRefresh)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string(Matchers.containsString(newAccess)))
                .andDo(result -> {
                    var setCookies = result.getResponse().getHeaders(HttpHeaders.SET_COOKIE);
                    org.hamcrest.MatcherAssert.assertThat(
                            setCookies,
                            Matchers.hasItem(Matchers.allOf(
                                    Matchers.containsString(REFRESH_COOKIE + "=" + newRefresh),
                                    Matchers.containsString("HttpOnly"),
                                    Matchers.containsString("SameSite=None"),
                                    Matchers.containsString("Path=/"),
                                    Matchers.containsString("Max-Age=" + Duration.ofDays(30).toSeconds())
                            ))
                    );
                });

        verify(refreshSessionUseCase).renovar(oldRefresh);
    }

    @Test
    void me_semAuth_deveRetornar401() throws Exception {
        mockMvc.perform(get("/auth/me"))
                .andExpect(status().isUnauthorized());
        verifyNoInteractions(usuarioUseCase);
    }

    @Test
    void logout_deveLimparRefreshCookie_eRetornar204() throws Exception {
        mockMvc.perform(post("/auth/logout"))
                .andExpect(status().isNoContent())
                .andExpect(header().string(HttpHeaders.SET_COOKIE,
                        Matchers.allOf(
                                Matchers.containsString(REFRESH_COOKIE + "="),
                                Matchers.containsString("Max-Age=0"),
                                Matchers.containsString("Path=/"),
                                Matchers.containsString("SameSite=None"),
                                Matchers.containsString("Secure"),
                                Matchers.containsString("HttpOnly")
                        )));
    }
}