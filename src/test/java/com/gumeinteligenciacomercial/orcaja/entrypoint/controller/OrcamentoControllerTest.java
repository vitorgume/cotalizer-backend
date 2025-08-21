package com.gumeinteligenciacomercial.orcaja.entrypoint.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gumeinteligenciacomercial.orcaja.application.usecase.ia.IaUseCase;
import com.gumeinteligenciacomercial.orcaja.domain.Plano;
import com.gumeinteligenciacomercial.orcaja.domain.StatusOrcamento;
import com.gumeinteligenciacomercial.orcaja.domain.TipoOrcamento;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.OrcamentoDto;
import com.gumeinteligenciacomercial.orcaja.infrastructure.repositories.OrcamentoRepository;
import com.gumeinteligenciacomercial.orcaja.infrastructure.repositories.UsuarioRepository;
import com.gumeinteligenciacomercial.orcaja.infrastructure.repositories.entities.OrcamentoEntity;
import com.gumeinteligenciacomercial.orcaja.infrastructure.repositories.entities.UsuarioEntity;
import org.bson.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
class OrcamentoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OrcamentoRepository orcamentoRepository;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    @MockitoBean
    private IaUseCase iaUseCase;

    @Captor
    ArgumentCaptor<OrcamentoEntity> orcamentoCaptor;

    private OrcamentoDto orcamentoDto;
    private OrcamentoEntity orcamento;
    private UsuarioEntity usuarioEntity;
    private Page<OrcamentoEntity> pageOrcamentos;

    @BeforeEach
    void setUp() {

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

        orcamento = OrcamentoEntity.builder()
                .id("id-teste-2")
                .conteudoOriginal("Conteudo teste 2")
                .orcamentoFormatado(new Document())
                .urlArquivo("url teste 2")
                .dataCriacao(LocalDate.now())
                .titulo("Titulo teste 2")
                .idUsuario("Id usuario teste 2")
                .status(StatusOrcamento.PENDENTE)
                .tipoOrcamento(TipoOrcamento.TRADICIONAL)
                .valorTotal(BigDecimal.valueOf(100))
                .build();

        usuarioEntity = UsuarioEntity.builder()
                .id("id-teste")
                .plano(Plano.GRATIS)
                .build();

        pageOrcamentos = new PageImpl<>(List.of(
                OrcamentoEntity.builder().id("C").build(),
                OrcamentoEntity.builder().id("C").build(),
                OrcamentoEntity.builder().id("C").build()
        ));
    }

    @Test
    void deveGerarOrcamentoComSucesso() throws Exception {
        orcamentoDto.setId(null);

        List<Map<String, Object>> itens = new ArrayList<>();

        Map<String, Object> item1 = new HashMap<>();
        item1.put("quantidade", 2);
        item1.put("valorUnitario", new BigDecimal("50.00"));  // 2 × 50 = 100
        itens.add(item1);

        Map<String, Object> item2 = new HashMap<>();
        item2.put("quantidade", 3);
        item2.put("valorUnitario", new BigDecimal("20.00"));  // 3 × 20 = 60
        itens.add(item2);

        Map<String, Object> orcamentoIA = new HashMap<>();
        orcamentoIA.put("itens", itens);

        orcamentoIA.put("desconto", "10%");

        Mockito.when(usuarioRepository.findById(anyString())).thenReturn(Optional.of(usuarioEntity));
        Mockito.when(orcamentoRepository.findByIdUsuario(anyString(), any())).thenReturn(pageOrcamentos);
        Mockito.when(iaUseCase.gerarOrcamento(anyString()))
                .thenReturn(new HashMap<>(orcamentoIA));
        Mockito.when(orcamentoRepository.save(any())).thenReturn(orcamento);

        mockMvc.perform(post("/orcamentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orcamentoDto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/orcamentos/" + orcamento.getId()))
                .andExpect(jsonPath("$.dado.id").value(orcamento.getId()));

        Mockito.verify(usuarioRepository).findById(anyString());
        Mockito.verify(orcamentoRepository).findByIdUsuario(anyString(), any());
        Mockito.verify(iaUseCase).gerarOrcamento(anyString());
        Mockito.verify(orcamentoRepository).save(any());
    }

    @Test
    void deveConsultarPorIdComSucesso() throws Exception {

        Mockito.when(orcamentoRepository.findById(anyString())).thenReturn(Optional.of(orcamento));

        mockMvc.perform(get("/orcamentos/" + orcamento.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dado.id").value(orcamento.getId()));

        Mockito.verify(orcamentoRepository).findById(anyString());
    }

    @Test
    void deveListarPorIdUsuarioComSucesso() throws Exception {

        Mockito.when(orcamentoRepository.findByIdUsuario(anyString(), any())).thenReturn(pageOrcamentos);

        mockMvc.perform(get("/orcamentos/usuario/idUsuarioTeste")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "dataCriacao,desc")
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.dado.number")
                        .value(0))
                .andExpect(jsonPath("$.dado.numberOfElements")
                        .value(pageOrcamentos.getTotalElements()));

        Mockito.verify(orcamentoRepository).findByIdUsuario(anyString(), any());
    }

    @Test
    void deveDeletarComSucesso() throws Exception {

        Mockito.when(orcamentoRepository.findById(anyString())).thenReturn(Optional.of(orcamento));
        Mockito.doNothing().when(orcamentoRepository).deleteById(anyString());

        mockMvc.perform(delete("/orcamentos/" + orcamento.getId()))
                .andExpect(status().isNoContent());

        Mockito.verify(orcamentoRepository).findById(anyString());
        Mockito.verify(orcamentoRepository).deleteById(anyString());
    }

    @Test
    void deveAlterarComSucesso() throws Exception {
        orcamentoDto.setTitulo("Titulo teste 3");
        Mockito.when(orcamentoRepository.findById(anyString())).thenReturn(Optional.of(orcamento));
        Mockito.when(orcamentoRepository.save(orcamentoCaptor.capture())).thenReturn(orcamento);

        mockMvc.perform(put("/orcamentos/" + orcamento.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orcamentoDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dado.id").value(orcamento.getId()))
                .andExpect(jsonPath("$.dado.titulo").value(orcamento.getTitulo()));


        Assertions.assertEquals(orcamentoDto.getTitulo(), orcamentoCaptor.getValue().getTitulo());

        Mockito.verify(orcamentoRepository).findById(anyString());
        Mockito.verify(orcamentoRepository).save(any());
    }
}