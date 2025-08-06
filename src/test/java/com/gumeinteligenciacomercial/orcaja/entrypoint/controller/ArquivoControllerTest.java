package com.gumeinteligenciacomercial.orcaja.entrypoint.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gumeinteligenciacomercial.orcaja.application.usecase.ArquivoUseCase;
import com.gumeinteligenciacomercial.orcaja.domain.Orcamento;
import com.gumeinteligenciacomercial.orcaja.domain.OrcamentoTradicional;
import com.gumeinteligenciacomercial.orcaja.domain.StatusOrcamento;
import com.gumeinteligenciacomercial.orcaja.domain.TipoOrcamento;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.CampoPersonalizadoDto;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.OrcamentoDto;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.OrcamentoTradicionalDto;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.ProdutoOrcamentoDto;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ArquivoController.class)
@AutoConfigureMockMvc(addFilters = false)
class ArquivoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // mockamos apenas o UseCase, evitando chamar PDF, disco etc.
    @MockitoBean
    private ArquivoUseCase arquivoUseCase;

    @Test
    void deveGerarArquivoOrcamento() throws Exception {
        // --- dado: DTO de request
        OrcamentoDto request = OrcamentoDto.builder()
                .conteudoOriginal("conteudo")
                .dataCriacao(LocalDate.of(2025, 8, 6))
                .orcamentoFormatado(Map.of("item", 1))
                .titulo("Orçamento Teste")
                .urlArquivo("")       // no request pode ficar vazio
                .usuarioId("user123")
                .status(StatusOrcamento.PENDENTE)
                .tipoOrcamento(TipoOrcamento.IA)
                .valorTotal(BigDecimal.valueOf(42.0))
                .build();

        // --- quando: o UseCase retorna este domínio
        Orcamento dominioRetorno = Orcamento.builder()
                .id("orc1")
                .conteudoOriginal(request.getConteudoOriginal())
                .dataCriacao(request.getDataCriacao())
                .orcamentoFormatado(request.getOrcamentoFormatado())
                .titulo(request.getTitulo())
                .urlArquivo("/files/orc1.pdf")
                .usuarioId(request.getUsuarioId())
                .status(request.getStatus())
                .tipoOrcamento(request.getTipoOrcamento())
                .valorTotal(request.getValorTotal())
                .build();
        given(arquivoUseCase.salvarArquivo(any(Orcamento.class)))
                .willReturn(dominioRetorno);

        // --- então: dispara POST e verifica status, header e JSON
        mockMvc.perform(post("/arquivos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/arquivos/orc1"))
                .andExpect(jsonPath("$.dado.id").value("orc1"))
                .andExpect(jsonPath("$.dado.urlArquivo").value("/files/orc1.pdf"));

        // --- e: confirma que o UseCase recebeu o domínio certo
        ArgumentCaptor<Orcamento> captor = ArgumentCaptor.forClass(Orcamento.class);
        verify(arquivoUseCase).salvarArquivo(captor.capture());
        assertThat(captor.getValue().getUsuarioId()).isEqualTo("user123");
    }

    @Test
    void deveGerarArquivoOrcamentoTradicional() throws Exception {
        OrcamentoTradicionalDto request = OrcamentoTradicionalDto.builder()
                .cliente("Cliente X")
                .cnpjCpf("12345678901")
                .produtos(List.of(
                        ProdutoOrcamentoDto.builder()
                                .descricao("Item A")
                                .quantidade(2)
                                .valor(BigDecimal.valueOf(10.0))
                                .build()
                ))
                .observacoes("Obs")
                .camposPersonalizados(List.of(
                        CampoPersonalizadoDto.builder()
                                .titulo("Cor")
                                .valor("Azul")
                                .build()
                ))
                .urlArquivo("")            // gerado pelo UseCase
                .idUsuario("user123")
                .valorTotal(BigDecimal.valueOf(20.0))
                .tipoOrcamento(TipoOrcamento.TRADICIONAL)
                .status(StatusOrcamento.PENDENTE)
                .dataCriacao(LocalDate.of(2025, 8, 6))
                .build();

        OrcamentoTradicional dominioRetorno = OrcamentoTradicional.builder()
                .id("trad1")
                .cliente(request.getCliente())
                .cnpjCpf(request.getCnpjCpf())
                // mapeie produtos e camposPersonalizados para domínio,
                // de acordo com seu mapper existente...
                .observacoes(request.getObservacoes())
                .urlArquivo("/files/trad1.pdf")
                .idUsuario(request.getIdUsuario())
                .dataCriacao(request.getDataCriacao())
                .valorTotal(request.getValorTotal())
                .tipoOrcamento(request.getTipoOrcamento())
                .status(request.getStatus())
                .build();

        given(arquivoUseCase.salvarArquivoTradicional(any(OrcamentoTradicional.class)))
                .willReturn(dominioRetorno);

        mockMvc.perform(post("/arquivos/tradicional")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/arquivos/tradicional/trad1"))
                .andExpect(jsonPath("$.dado.id").value("trad1"))
                .andExpect(jsonPath("$.dado.urlArquivo").value("/files/trad1.pdf"));
    }

    @Test
    void deveAcessarArquivoInline() throws Exception {
        byte[] pdf = "conteudo pdf".getBytes();
        Resource resource = new ByteArrayResource(pdf) {
            @Override public String getFilename() { return "teste.pdf"; }
        };
        given(arquivoUseCase.acessarArquivo("teste.pdf")).willReturn(resource);

        mockMvc.perform(get("/arquivos/acessar/teste.pdf"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=teste.pdf"))
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(content().bytes(pdf));
    }

    @Test
    void deveFazerDownloadArquivoAttachment() throws Exception {
        byte[] pdf = "conteudo download".getBytes();
        Resource resource = new ByteArrayResource(pdf) {
            @Override public String getFilename() { return "down.pdf"; }
        };
        given(arquivoUseCase.downloadArquivo("down.pdf")).willReturn(resource);

        mockMvc.perform(get("/arquivos/download/down.pdf"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=down.pdf"))
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(content().bytes(pdf));
    }

    @Test
    void deveCadastrarLogo() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "logo", "logo.png", MediaType.IMAGE_PNG_VALUE, "imagem".getBytes());

        given(arquivoUseCase.cadastrarLogo("user123", file))
                .willReturn("/files/logos/user123.png");

        mockMvc.perform(multipart("/arquivos/logo")
                        .file(file)
                        .param("idUsuario", "user123"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/logos/user123"))
                .andExpect(jsonPath("$.dado.urlFoto").value("/files/logos/user123.png"))
                .andExpect(jsonPath("$.dado.idUsuario").value("user123"));
    }

}