package com.gumeinteligenciacomercial.orcaja.entrypoint.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gumeinteligenciacomercial.orcaja.application.usecase.CodigoAlteracaoSenhaUseCase;
import com.gumeinteligenciacomercial.orcaja.application.usecase.EmailUseCase;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.SolicitacaoNovaSenhaDto;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(
        properties = {
                "spring.task.scheduling.enabled=false",
                "openia.api.key=TEST_OPENAI_KEY",
                "security.api.key=TEST_SIGNATURES_KEY",
                "secret.key=SECRET_KEY_TEST"
        }
)
@AutoConfigureMockMvc(addFilters = false)
class SenhaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    @MockitoBean
    private EmailUseCase emailUseCase;

    @MockitoBean
    private CodigoAlteracaoSenhaUseCase codigoAlteracaoSenhaUseCase;

    private SolicitacaoNovaSenhaDto solicitacaoNovaSenhaDto;
    private UsuarioEntity usuarioEntity;

    @BeforeEach
    void setUp() {
        solicitacaoNovaSenhaDto = new SolicitacaoNovaSenhaDto("emailteste@gmail.com");
        usuarioEntity = UsuarioEntity.builder()
                .id("idteste")
                .build();
    }

    @Test
    void deveSolicitarNovaSenhaComSucesso() throws Exception {

        Mockito.when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.of(usuarioEntity));
        Mockito.doNothing().when(emailUseCase).enviarAlteracaoDeSenha(anyString(), anyString());
        Mockito.doNothing().when(codigoAlteracaoSenhaUseCase).adicionarAoCache(anyString(), anyString());

        mockMvc.perform(post("/senhas/solicitar/nova")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(solicitacaoNovaSenhaDto)))
                .andExpect(status().isOk());

        Mockito.verify(usuarioRepository).findByEmail(anyString());
        Mockito.verify(emailUseCase).enviarAlteracaoDeSenha(anyString(), anyString());
        Mockito.verify(codigoAlteracaoSenhaUseCase).adicionarAoCache(anyString(), anyString());
    }
}