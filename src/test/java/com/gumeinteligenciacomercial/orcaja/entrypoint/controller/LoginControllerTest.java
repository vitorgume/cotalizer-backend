package com.gumeinteligenciacomercial.orcaja.entrypoint.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gumeinteligenciacomercial.orcaja.application.gateway.LoginGateway;
import com.gumeinteligenciacomercial.orcaja.application.usecase.CriptografiaUseCase;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.LoginDto;
import com.gumeinteligenciacomercial.orcaja.infrastructure.repositories.UsuarioRepository;
import com.gumeinteligenciacomercial.orcaja.infrastructure.repositories.entities.UsuarioEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        properties = {
                "spring.task.scheduling.enabled=false",
                "openia.api.key=TEST_OPENAI_KEY",
                "security.api.key=TEST_SIGNATURES_KEY",
                "secret.key=SECRET_KEY_TEST"
        }
)
@AutoConfigureMockMvc(addFilters = false)
class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    @MockitoBean
    private LoginGateway loginGateway;

    @MockitoBean
    private CriptografiaUseCase criptografiaUseCase;

    private LoginDto loginDto;
    private UsuarioEntity usuario;

    @BeforeEach
    void setUp() {
        loginDto = LoginDto.builder()
                .email("emailteste@gmail.com")
                .senha("senhateste123")
                .build();

        usuario = UsuarioEntity.builder()
                .id("id-teste")
                .email("emailteste@gmail.com")
                .senha("senhateste123")
                .build();
    }

    @Test
    void deveLogarComSucesso() throws Exception {

        Mockito.when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.of(usuario));
        Mockito.when(criptografiaUseCase.validaSenha(anyString(), anyString())).thenReturn(true);
        Mockito.when(loginGateway.generateToken(anyString(), anyString())).thenReturn("token-teste");

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dado.email").value(loginDto.getEmail()))
                .andExpect(jsonPath("$.dado.senha").isEmpty())
                .andExpect(jsonPath("$.dado.token").value("token-teste"))
                .andExpect(jsonPath("$.dado.usuarioId").value(usuario.getId()));

        Mockito.verify(usuarioRepository).findByEmail(anyString());
        Mockito.verify(criptografiaUseCase).validaSenha(anyString(), anyString());
        Mockito.verify(loginGateway).generateToken(anyString(), anyString());
    }
}