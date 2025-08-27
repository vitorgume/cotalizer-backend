package com.gumeinteligenciacomercial.orcaja.entrypoint.controller.arquivo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gumeinteligenciacomercial.orcaja.application.gateway.ArquivoGateway;
import com.gumeinteligenciacomercial.orcaja.application.usecase.*;
import com.gumeinteligenciacomercial.orcaja.domain.StatusOrcamento;
import com.gumeinteligenciacomercial.orcaja.domain.TipoOrcamento;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.CampoPersonalizadoDto;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.OrcamentoDto;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.OrcamentoTradicionalDto;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.ProdutoOrcamentoDto;
import com.gumeinteligenciacomercial.orcaja.infrastructure.dataprovider.AssinaturaDataProvider;
import com.gumeinteligenciacomercial.orcaja.infrastructure.repositories.OrcamentoRepository;
import com.gumeinteligenciacomercial.orcaja.infrastructure.repositories.OrcamentoTradicionalRepository;
import com.gumeinteligenciacomercial.orcaja.infrastructure.repositories.UsuarioRepository;
import com.gumeinteligenciacomercial.orcaja.infrastructure.repositories.entities.*;
import org.bson.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
class ArquivoApiControllerInteTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ArquivoGateway arquivoGateway;

    @MockitoBean
    private OrcamentoRepository orcamentoRepository;

    @MockitoBean
    private OrcamentoTradicionalRepository orcamentoTradicionalRepository;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    @MockitoBean
    private HtmlUseCase htmlUseCase;

    @MockitoBean
    private AssinaturaDataProvider assinaturaDataProvider;

    @Captor
    ArgumentCaptor<OrcamentoEntity> orcamentoCaptor;

    @Captor
    ArgumentCaptor<OrcamentoTradicionalEntity> orcamentoTradicionalCaptor;

    @Captor
    ArgumentCaptor<UsuarioEntity> usuarioCaptor;

    private OrcamentoEntity orcamento;
    private OrcamentoDto orcamentoDto;
    private OrcamentoTradicionalEntity orcamentoTradicional;
    private OrcamentoTradicionalDto orcamentoTradicionalDto;
    private UsuarioEntity usuario;

    @BeforeEach
    void setup() {
        orcamento = OrcamentoEntity.builder()
                .id("id-teste-2")
                .conteudoOriginal("Conteudo teste")
                .orcamentoFormatado(new Document())
                .urlArquivo("url teste")
                .dataCriacao(LocalDate.now())
                .titulo("Titulo teste")
                .idUsuario("Id usuario teste")
                .status(StatusOrcamento.PENDENTE)
                .tipoOrcamento(TipoOrcamento.TRADICIONAL)
                .valorTotal(BigDecimal.valueOf(100))
                .build();

        orcamentoDto = OrcamentoDto.builder()
                .id("id-teste-2")
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

        orcamentoTradicional = OrcamentoTradicionalEntity.builder()
                .id("id-teste-2")
                .cliente("Cliente teste 2")
                .cnpjCpf("cnpj/cpf teste 2")
                .produtos(List.of(ProdutoOrcamentoEntity.builder().descricao("descricao-teste").build(), ProdutoOrcamentoEntity.builder().descricao("descricao-teste-2").build()))
                .observacoes("Observações teste")
                .camposPersonalizados(List.of(CampoPersonalizadoEntity.builder().titulo("Titulo teste").valor("Valor teste").build(), CampoPersonalizadoEntity.builder().titulo("Titulo teste 2").valor("Valor teste 2").build()))
                .urlArquivo("url teste 2")
                .idUsuario("id-usuario-test-2")
                .valorTotal(BigDecimal.valueOf(100))
                .tipoOrcamento(TipoOrcamento.TRADICIONAL)
                .status(StatusOrcamento.PENDENTE)
                .dataCriacao(LocalDate.now())
                .build();

        orcamentoTradicionalDto = OrcamentoTradicionalDto.builder()
                .id("id-teste-2")
                .cliente("Cliente teste")
                .cnpjCpf("cnpj/cpf teste")
                .produtos(List.of(ProdutoOrcamentoDto.builder().descricao("descricao-teste").build(), ProdutoOrcamentoDto.builder().descricao("descricao-teste-2").build()))
                .observacoes("Observações teste")
                .camposPersonalizados(List.of(CampoPersonalizadoDto.builder().titulo("Titulo teste").valor("Valor teste").build(), CampoPersonalizadoDto.builder().titulo("Titulo teste 2").valor("Valor teste 2").build()))
                .urlArquivo("url teste")
                .idUsuario("id-usuario-test")
                .valorTotal(BigDecimal.valueOf(100))
                .tipoOrcamento(TipoOrcamento.TRADICIONAL)
                .status(StatusOrcamento.PENDENTE)
                .dataCriacao(LocalDate.now())
                .build();

        usuario = UsuarioEntity.builder().id("id-teste").email("emailteste@gmail.com").urlLogo("urlteste").build();
    }

    @Test
    void gerarArquivoOrcamentoDeveRetornarCreated() throws Exception {

        when(htmlUseCase.gerarHtml(any(), anyString())).thenReturn("htmlteste");

        when(arquivoGateway.salvarPdf(anyString(), any()))
                .thenReturn("urlteste");

        when(orcamentoRepository.findById(eq(orcamentoDto.getId())))
                .thenReturn(Optional.of(orcamento));

        when(orcamentoRepository.save(orcamentoCaptor.capture())).thenReturn(orcamento);

        mockMvc.perform(post("/arquivos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orcamentoDto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/arquivos/id-teste-2"))
                .andExpect(jsonPath("$.dado.id").value("id-teste-2"))
                .andExpect(jsonPath("$.dado.urlArquivo").value("url teste"));

        verify(htmlUseCase).gerarHtml(any(), anyString());
        verify(arquivoGateway).salvarPdf(anyString(), anyString());
        verify(orcamentoRepository, times(2)).findById(eq(orcamentoDto.getId()));
        verify(orcamentoRepository).save(any());

        Assertions.assertEquals("urlteste", orcamentoCaptor.getValue().getUrlArquivo());
    }

    @Test
    void gerarArquivoOrcamentoTradicionalDeveRetornarCreated() throws Exception {

        when(htmlUseCase.gerarHtmlTradicional(any())).thenReturn("htmlteste");

        when(arquivoGateway.salvarPdf(anyString(), any()))
                .thenReturn("urlteste");

        when(orcamentoTradicionalRepository.findById(eq(orcamentoTradicionalDto.getId())))
                .thenReturn(Optional.of(orcamentoTradicional));

        when(orcamentoTradicionalRepository.save(orcamentoTradicionalCaptor.capture())).thenReturn(orcamentoTradicional);

        mockMvc.perform(post("/arquivos/tradicional")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orcamentoTradicionalDto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/arquivos/tradicional/id-teste-2"))
                .andExpect(jsonPath("$.dado.id").value("id-teste-2"));

        verify(htmlUseCase).gerarHtmlTradicional(any());
        verify(arquivoGateway).salvarPdf(anyString(), anyString());
        verify(orcamentoTradicionalRepository, times(2)).findById(eq(orcamentoTradicionalDto.getId()));
        verify(orcamentoTradicionalRepository).save(any());

        Assertions.assertEquals("urlteste", orcamentoTradicionalCaptor.getValue().getUrlArquivo());
    }

    @Test
    void cadastrarLogoDeveRetornarCreated() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "logo", "image.png", MediaType.IMAGE_PNG_VALUE, "imgdata".getBytes());

        when(usuarioRepository.findById(eq(usuario.getId()))).thenReturn(Optional.of(usuario));
        when(arquivoGateway.salvarLogo(anyString(), any())).thenReturn("url-logo-teste");
        when(usuarioRepository.save(usuarioCaptor.capture())).thenReturn(usuario);

        mockMvc.perform(multipart("/arquivos/logo")
                        .file(file)
                        .param("idUsuario", usuario.getId()))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/logos/" + usuario.getId()))
                .andExpect(jsonPath("$.dado.urlFoto").value("url-logo-teste"))
                .andExpect(jsonPath("$.dado.idUsuario").value(usuario.getId()));

        verify(arquivoGateway).salvarLogo(anyString(), any());
        verify(usuarioRepository, times(2)).findById(eq(usuario.getId()));
        verify(usuarioRepository).save(any());

        Assertions.assertEquals("url-logo-teste", usuarioCaptor.getValue().getUrlLogo());
    }

}