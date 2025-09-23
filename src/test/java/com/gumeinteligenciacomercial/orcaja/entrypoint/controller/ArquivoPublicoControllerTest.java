package com.gumeinteligenciacomercial.orcaja.entrypoint.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gumeinteligenciacomercial.orcaja.application.usecase.ArquivoUseCase;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
                "app.id.prompt.ia.gerador-orcamento=teste",
                "app.id.prompt.ia.interpretador-prompt=teste"
        }
)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class ArquivoPublicoControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    ArquivoUseCase useCase;

    @MockitoBean
    JavaMailSender javaMailSender;

    @Test
    void acessarArquivoPdfInlineContentTypePdfEFilenameCorreto() throws Exception {
        String nomeArquivo = "pasta/sub/arquivo.PDF";
        byte[] bytes = "PDF_BYTES".getBytes();
        Resource resource = new ByteArrayResource(bytes);

        given(useCase.acessarArquivo(eq(nomeArquivo))).willReturn(resource);

        mockMvc.perform(get("/arquivos/acessar/{nome}", nomeArquivo))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION,
                        Matchers.allOf(
                                Matchers.containsString("inline"),
                                Matchers.containsString("filename=\"arquivo.PDF\"")
                        )))
                .andExpect(header().string("Accept-Ranges", "bytes"))
                .andExpect(content().contentType(MediaType.APPLICATION_PDF));

        verify(useCase).acessarArquivo(eq("/" + nomeArquivo));
    }

    @Test
    void acessarArquivoPngInlineContentTypePng() throws Exception {
        String nomeArquivo = "imgs/icon.png";
        byte[] bytes = new byte[]{1, 2, 3};
        Resource resource = new ByteArrayResource(bytes);

        given(useCase.acessarArquivo(eq(nomeArquivo))).willReturn(resource);

        mockMvc.perform(get("/arquivos/acessar/{nome}", nomeArquivo))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION,
                        Matchers.allOf(
                                Matchers.containsString("inline"),
                                Matchers.containsString("filename=\"icon.png\"")
                        )))
                .andExpect(header().string("Accept-Ranges", "bytes"))
                .andExpect(content().contentType(MediaType.IMAGE_PNG));

        verify(useCase).acessarArquivo(eq("/" + nomeArquivo));
    }

    @Test
    void downloadArquivoJpegAttachmentContentTypeJpegEPrefixoPdfNoGateway() throws Exception {
        String nomeArquivo = "relatorio/2025/arquivo.jpg";
        byte[] bytes = new byte[]{9, 9, 9};
        Resource resource = new ByteArrayResource(bytes);

        given(useCase.downloadArquivo(eq("pdf" + nomeArquivo))).willReturn(resource);

        mockMvc.perform(get("/arquivos/download/{nome}", nomeArquivo))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION,
                        Matchers.allOf(
                                Matchers.containsString("attachment"),
                                Matchers.containsString("filename=\"arquivo.jpg\"")
                        )))
                .andExpect(content().contentType(MediaType.IMAGE_JPEG));

        verify(useCase).downloadArquivo(eq("pdf/" + nomeArquivo));
    }

    @Test
    void downloadArquivoExtDesconhecidaContentTypeOctetStream() throws Exception {
        String nomeArquivo = "docs/readme.bin";
        byte[] bytes = new byte[]{7, 7};
        Resource resource = new ByteArrayResource(bytes);

        given(useCase.downloadArquivo(eq("pdf" + nomeArquivo))).willReturn(resource);

        mockMvc.perform(get("/arquivos/download/{nome}", nomeArquivo))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION,
                        Matchers.allOf(
                                Matchers.containsString("attachment"),
                                Matchers.containsString("filename=\"readme.bin\"")
                        )))
                .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM));

        verify(useCase).downloadArquivo(eq("pdf/" + nomeArquivo));
    }
}