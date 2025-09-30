package com.gumeinteligenciacomercial.orcaja.entrypoint.controller;

import com.gumeinteligenciacomercial.orcaja.application.usecase.EmailUseCase;
import com.gumeinteligenciacomercial.orcaja.domain.Template;
import com.gumeinteligenciacomercial.orcaja.infrastructure.repositories.TemplateRepository;
import com.gumeinteligenciacomercial.orcaja.infrastructure.repositories.entities.TemplateEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import static org.mockito.BDDMockito.given;

import static org.junit.jupiter.api.Assertions.*;

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
                "app.storage.s3.endpoint=http://localhost:4566",
                "app.id.prompt.ia.gerador-orcamento=teste",
                "app.id.prompt.ia.interpretador-prompt=teste"
        }
)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class TemplateControllerTest {

    @MockitoBean
    private TemplateRepository repository;

    @MockitoBean
    private EmailUseCase emailUseCase;

    @Autowired
    private MockMvc mockMvc;

    private List<TemplateEntity> templates;

    @BeforeEach
    void setUp() {
        templates = List.of(
                TemplateEntity.builder().id("teste").nomeArquivo("teste").build(),
                TemplateEntity.builder().id("teste").nomeArquivo("teste").build()
        );
    }

    @Test
    void deveListaTodosComSucesso() throws Exception {
        given(repository.findAll()).willReturn(templates);

        mockMvc.perform(get("/templates"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dado.[0].id").value(templates.get(0).getId()));

    }
}