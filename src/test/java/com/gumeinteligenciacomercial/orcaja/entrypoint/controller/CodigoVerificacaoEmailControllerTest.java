package com.gumeinteligenciacomercial.orcaja.entrypoint.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.VerificacaoEmailDto;
import com.gumeinteligenciacomercial.orcaja.infrastructure.repositories.UsuarioRepository;
import com.gumeinteligenciacomercial.orcaja.infrastructure.repositories.entities.UsuarioEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

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
                "app.security.csrf.secure=false",
                "app.security.csrf.sameSite=None",
                "app.id.prompt.ia.gerador-orcamento=teste",
                "app.id.prompt.ia.interpretador-prompt=teste"
        }
)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class CodigoVerificacaoEmailControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RedisTemplate<String, String> redisTemplate;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    @MockitoBean
    private JavaMailSender javaMailSender;

    private VerificacaoEmailDto verificacaoEmailDto;
    private UsuarioEntity usuario;
    private ValueOperations<String, String> valueOps;

    @BeforeEach
    void setUp() {
        verificacaoEmailDto = VerificacaoEmailDto.builder()
                .email("emailteste@gmail.com")
                .codigo("codigoteste123")
                .build();

        usuario = UsuarioEntity.builder()
                .id("id-teste")
                .email("emailteste@gmail.com")
                .build();

        valueOps = Mockito.mock(ValueOperations.class);
    }

    @Test
    void deveVerificarEmailComSucesso() throws Exception {

        Mockito.when(redisTemplate.opsForValue()).thenReturn(valueOps);
        Mockito.when(redisTemplate.opsForValue().get(anyString())).thenReturn("codigoteste123");
        Mockito.when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.of(usuario));
        Mockito.when(usuarioRepository.findById(anyString())).thenReturn(Optional.of(usuario));
        Mockito.when(usuarioRepository.save(any())).thenReturn(usuario);

        mockMvc.perform(post("/verificaoes/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(verificacaoEmailDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dado.email").value("emailteste@gmail.com"))
                .andExpect(jsonPath("$.dado.codigo").isEmpty());

        Mockito.verify(usuarioRepository).findByEmail(anyString());
        Mockito.verify(usuarioRepository).findById(anyString());
        Mockito.verify(usuarioRepository).save(any());

    }
}