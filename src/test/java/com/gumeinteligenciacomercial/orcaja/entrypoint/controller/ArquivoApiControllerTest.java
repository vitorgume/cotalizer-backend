package com.gumeinteligenciacomercial.orcaja.entrypoint.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gumeinteligenciacomercial.orcaja.application.usecase.ArquivoUseCase;
import com.gumeinteligenciacomercial.orcaja.domain.CampoPersonalizado;
import com.gumeinteligenciacomercial.orcaja.domain.Orcamento;
import com.gumeinteligenciacomercial.orcaja.domain.OrcamentoTradicional;
import com.gumeinteligenciacomercial.orcaja.domain.ProdutoOrcamento;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
                "app.security.csrf.secure=false",
                "app.security.csrf.sameSite=None",
                "app.storage.s3.endpoint=http://localhost:4566",
                "app.id.prompt.ia.gerador-orcamento=teste",
                "app.id.prompt.ia.interpretador-prompt=teste"
        }
)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class ArquivoApiControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    ArquivoUseCase arquivoUseCase;

    @MockitoBean
    JavaMailSender javaMailSender;

    @Test
    void gerarArquivoOrcamentoDeveRetornar201LocationEBody() throws Exception {
        var requestJson = objectMapper.writeValueAsString(Map.of(
                "id", "orc-1",
                "usuarioId", "usr-1",
                "orcamentoFormatado", Map.of("k", "v")
        ));

        Orcamento salvo = Orcamento.builder()
                .id("orc-1")
                .usuarioId("usr-1")
                .urlArquivo("https://files/ARQ-abcde.pdf")
                .dataCriacao(LocalDate.now())
                .valorTotal(new BigDecimal("100.00"))
                .build();

        given(arquivoUseCase.salvarArquivo(any(Orcamento.class))).willReturn(salvo);

        mockMvc.perform(post("/api/arquivos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/arquivos/orc-1"))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.dado.id").value("orc-1"))
                .andExpect(jsonPath("$.dado.urlArquivo").value("https://files/ARQ-abcde.pdf"));

        verify(arquivoUseCase).salvarArquivo(any(Orcamento.class));
    }

    @Test
    void gerarArquivoOrcamentoTradicionalDeveRetornar201LocationEBody() throws Exception {
        var requestJson = objectMapper.writeValueAsString(Map.of(
                "id", "ot-1",
                "idUsuario", "usr-9",
                "cliente", "Cliente X",
                "produtos", List.of(),
                "camposPersonalizados", List.of()
        ));

        OrcamentoTradicional salvo = OrcamentoTradicional.builder()
                .id("ot-1")
                .idUsuario("usr-9")
                .cliente("Cliente X")
                .urlArquivo("https://files/ARQ-xxxxx.pdf")
                .dataCriacao(LocalDate.now())
                .produtos(List.of(ProdutoOrcamento.builder().descricao("teste").valor(new BigDecimal(20)).quantidade(2).build()))
                .camposPersonalizados(List.of(CampoPersonalizado.builder().titulo("teste").valor("teste").build()))
                .build();

        given(arquivoUseCase.salvarArquivoTradicional(any(OrcamentoTradicional.class)))
                .willReturn(salvo);

        mockMvc.perform(post("/api/arquivos/tradicional")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/arquivos/tradicional/ot-1"))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.dado.id").value("ot-1"))
                .andExpect(jsonPath("$.dado.urlArquivo").value("https://files/ARQ-xxxxx.pdf"));

        verify(arquivoUseCase).salvarArquivoTradicional(any(OrcamentoTradicional.class));
    }

    @Test
    void cadastrarLogoDeveRetornar201LocationEBody() throws Exception {
        String idUsuario = "u-1";
        MockMultipartFile file = new MockMultipartFile(
                "logo", "logo.png", "image/png", new byte[]{1, 2, 3}
        );

        given(arquivoUseCase.cadastrarLogo(idUsuario, file))
                .willReturn("logos/u-1/logo.png");

        mockMvc.perform(multipart("/api/arquivos/logo")
                        .file(file)
                        .param("idUsuario", idUsuario))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/logos/u-1"))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.dado.idUsuario").value("u-1"))
                .andExpect(jsonPath("$.dado.urlFoto").value("logos/u-1/logo.png"));

        verify(arquivoUseCase).cadastrarLogo(idUsuario, file);
    }

    @Test
    void deveDeletarArquivoERetornar204() throws Exception {
        String nomeArquivo = "/pasta/arquivo.";

        BDDMockito.doNothing().when(arquivoUseCase).deletaArquivo(nomeArquivo);

        mockMvc.perform(delete("/api/arquivos/arquivo/" + nomeArquivo))
                .andExpect(status().isNoContent());

        verify(arquivoUseCase).deletaArquivo(nomeArquivo);
    }

    @Test
    void deveDeletarLogoERetornar204() throws Exception {
        String nomeArquivo = "/pasta/logo.";

        BDDMockito.doNothing().when(arquivoUseCase).deletarLogo(nomeArquivo);

        mockMvc.perform(delete("/api/arquivos/logo/" + nomeArquivo))
                .andExpect(status().isNoContent());

        verify(arquivoUseCase).deletarLogo(nomeArquivo);
    }
}