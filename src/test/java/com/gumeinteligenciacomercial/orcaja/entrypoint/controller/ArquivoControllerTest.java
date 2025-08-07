package com.gumeinteligenciacomercial.orcaja.entrypoint.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gumeinteligenciacomercial.orcaja.application.gateway.ArquivoGateway;
import com.gumeinteligenciacomercial.orcaja.application.usecase.*;
import com.gumeinteligenciacomercial.orcaja.domain.Orcamento;
import com.gumeinteligenciacomercial.orcaja.domain.StatusOrcamento;
import com.gumeinteligenciacomercial.orcaja.domain.TipoOrcamento;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.OrcamentoDto;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.OrcamentoTradicionalDto;
import com.gumeinteligenciacomercial.orcaja.entrypoint.mapper.OrcamentoMapper;
import com.gumeinteligenciacomercial.orcaja.entrypoint.mapper.OrcamentoTradicionalMapper;
import com.gumeinteligenciacomercial.orcaja.infrastructure.dataprovider.ArquivoDataProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class ArquivoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ArquivoGateway arquivoGateway;

    @MockitoBean
    private OrcamentoUseCase orcamentoUseCase;

    @MockitoBean
    private OrcamentoTradicionalUseCase orcamentoTradicionalUseCase;

    @MockitoBean
    private UsuarioUseCase usuarioUseCase;

    @MockitoBean
    private HtmlUseCase htmlUseCase;

    @Captor
    ArgumentCaptor<Orcamento> orcamentoCaptor;

    private Orcamento orcamento;
    private OrcamentoDto orcamentoDto;
    private OrcamentoTradicionalDto tradDto;
    private OrcamentoTradicionalDto tradDtoResult;

    @BeforeEach
    void setup() {
        orcamento = Orcamento.builder()
                .id("id-teste-2")
                .conteudoOriginal("Conteudo teste")
                .orcamentoFormatado(Map.of())
                .urlArquivo("url teste")
                .dataCriacao(LocalDate.now())
                .titulo("Titulo teste")
                .usuarioId("Id usuario teste")
                .status(StatusOrcamento.PENDENTE)
                .tipoOrcamento(TipoOrcamento.TRADICIONAL)
                .valorTotal(BigDecimal.valueOf(100))
                .build();

        orcamentoDto = OrcamentoDto.builder()
                .conteudoOriginal("Conteudo teste 2")
                .orcamentoFormatado(Map.of())
                .urlArquivo("url teste 2")
                .dataCriacao(LocalDate.now())
                .titulo("Titulo teste 2")
                .usuarioId("Id usuario teste 2")
                .status(StatusOrcamento.APROVADO)
                .tipoOrcamento(TipoOrcamento.TRADICIONAL)
                .valorTotal(BigDecimal.valueOf(110))
                .build();
    }

    @Test
    void gerarArquivoOrcamento_deveRetornarCreated() throws Exception {

        given(htmlUseCase.gerarHtml(any(), anyString())).willReturn("htmlteste");

        given(arquivoGateway.salvarPdf(anyString(), any()))
                .willReturn("urlteste");

        given(orcamentoUseCase.consultarPorId(anyString()))
                .willReturn(orcamento);

        given(orcamentoUseCase.alterar(anyString(), orcamentoCaptor.capture())).willReturn(orcamento);

        mockMvc.perform(post("/arquivos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orcamentoDto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/arquivos/o1"))
                .andExpect(jsonPath("$.dado.id").value("id-teste-2"));

        verify(htmlUseCase).gerarHtml(any(), anyString());
        verify(arquivoGateway).salvarPdf(anyString(), anyString());
        verify(orcamentoUseCase).consultarPorId(anyString());
        verify(orcamentoUseCase).alterar(anyString(), any());

        Assertions.assertEquals("urlteste", orcamentoCaptor.getValue().getUrlArquivo());
    }

//    @Test
//    void gerarArquivoOrcamentoTradicional_deveRetornarCreated() throws Exception {
//        // given
//        given(arquivoUseCase.salvarArquivoTradicional(any()))
//                .willReturn(OrcamentoTradicionalMapper.paraDomain(tradDtoResult));
//
//        // when & then
//        mockMvc.perform(post("/arquivos/tradicional")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(tradDto)))
//                .andExpect(status().isCreated())
//                .andExpect(header().string("Location", "/arquivos/tradicional/t1"))
//                .andExpect(jsonPath("$.data.id").value("t1"));
//
//        then(arquivoUseCase).should().salvarArquivoTradicional(any());
//    }
//
//    @Test
//    void acessarArquivo_deveRetornarInlinePdf() throws Exception {
//        // given
//        byte[] data = "pdf content".getBytes();
//        Resource res = new ByteArrayResource(data) {
//            @Override public String getFilename() { return "file.pdf"; }
//        };
//        given(arquivoUseCase.acessarArquivo("file.pdf")).willReturn(res);
//
//        // when & then
//        mockMvc.perform(get("/arquivos/acessar/file.pdf"))
//                .andExpect(status().isOk())
//                .andExpect(header().string("Content-Disposition", "inline; filename=file.pdf"))
//                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
//                .andExpect(content().bytes(data));
//
//        then(arquivoUseCase).should().acessarArquivo("file.pdf");
//    }
//
//    @Test
//    void downloadArquivo_deveRetornarAttachmentPdf() throws Exception {
//        // given
//        byte[] data = "pdf data".getBytes();
//        Resource res = new ByteArrayResource(data) {
//            @Override public String getFilename() { return "doc.pdf"; }
//        };
//        given(arquivoUseCase.downloadArquivo("doc.pdf")).willReturn(res);
//
//        // when & then
//        mockMvc.perform(get("/arquivos/download/doc.pdf"))
//                .andExpect(status().isOk())
//                .andExpect(header().string("Content-Disposition", "attachment; filename=doc.pdf"))
//                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
//                .andExpect(content().bytes(data));
//
//        then(arquivoUseCase).should().downloadArquivo("doc.pdf");
//    }
//
//    @Test
//    void cadastrarLogo_deveRetornarCreated() throws Exception {
//        // given
//        MockMultipartFile file = new MockMultipartFile(
//                "logo", "image.png", MediaType.IMAGE_PNG_VALUE, "imgdata".getBytes());
//        given(arquivoUseCase.cadastrarLogo("u123", file))
//                .willReturn("/path/to/u123/image.png");
//
//        // when & then
//        mockMvc.perform(multipart("/arquivos/logo")
//                        .file(file)
//                        .param("idUsuario", "u123"))
//                .andExpect(status().isCreated())
//                .andExpect(header().string("Location", "/logos/null"))
//                .andExpect(jsonPath("$.data.urlFoto").value("/path/to/u123/image.png"));
//
//        then(arquivoUseCase).should().cadastrarLogo(eq("u123"), any());
//    }

}