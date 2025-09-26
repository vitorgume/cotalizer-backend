package com.gumeinteligenciacomercial.orcaja.entrypoint.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.AvaliacaoDto;
import com.gumeinteligenciacomercial.orcaja.infrastructure.repositories.UsuarioRepository;
import com.gumeinteligenciacomercial.orcaja.infrastructure.repositories.entities.PlanoEntity;
import com.gumeinteligenciacomercial.orcaja.infrastructure.repositories.entities.UsuarioEntity;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(
        properties = {
                "spring.task.scheduling.enabled=false",
                "openia.api.key=TEST_OPENAI_KEY",
                "security.api.key=TEST_SIGNATURES_KEY",
                "secret.key=5a6bf2660e4a4fb7ec956e43959e4e6f826a9662a1f4578bcab89e3178770615"
        }
)
@AutoConfigureMockMvc(addFilters = false)
class AvaliacaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    @MockitoBean
    private JavaMailSender mailSender;

    private AvaliacaoDto avaliacaoDto;
    private UsuarioEntity usuarioEntity;

    @BeforeEach
    void setUp() {
        avaliacaoDto = AvaliacaoDto.builder()
                .idUsuario("id-teste")
                .nota(2)
                .sugestaoMelhoria("melhoria teste")
                .motivoNota("motivo teste")
                .build();

        usuarioEntity = UsuarioEntity.builder().id("id-teste").plano(PlanoEntity.builder().id("idteste123").build()).nome("Nome teste").email("emailteste@gmail.com").build();
    }

    @Test
    void deveEnviarComSucesso() throws Exception {
        Mockito.when(usuarioRepository.findById(anyString()))
                .thenReturn(Optional.of(usuarioEntity));
        Mockito.when(usuarioRepository.save(any()))
                .thenReturn(usuarioEntity);

        Mockito.doNothing().when(mailSender).send(Mockito.any(SimpleMailMessage.class));

        mockMvc.perform(post("/avaliacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(avaliacaoDto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/avaliacoes/" + avaliacaoDto.getIdUsuario()))
                .andExpect(jsonPath("$.dado.nota").value(avaliacaoDto.getNota()));

        Mockito.verify(usuarioRepository, Mockito.times(2)).findById(anyString());
        Mockito.verify(usuarioRepository).save(any());
        Mockito.verify(mailSender).send(Mockito.any(SimpleMailMessage.class));
    }
}