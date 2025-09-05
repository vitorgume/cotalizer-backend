package com.gumeinteligenciacomercial.orcaja.entrypoint.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gumeinteligenciacomercial.orcaja.domain.OrcamentoTradicional;
import com.gumeinteligenciacomercial.orcaja.domain.Plano;
import com.gumeinteligenciacomercial.orcaja.domain.StatusOrcamento;
import com.gumeinteligenciacomercial.orcaja.domain.TipoOrcamento;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.CampoPersonalizadoDto;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.OrcamentoTradicionalDto;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.ProdutoOrcamentoDto;
import com.gumeinteligenciacomercial.orcaja.infrastructure.repositories.OrcamentoRepository;
import com.gumeinteligenciacomercial.orcaja.infrastructure.repositories.OrcamentoTradicionalRepository;
import com.gumeinteligenciacomercial.orcaja.infrastructure.repositories.UsuarioRepository;
import com.gumeinteligenciacomercial.orcaja.infrastructure.repositories.entities.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
class OrcamentoTradicionalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OrcamentoTradicionalRepository orcamentoTradicionalRepository;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    @MockitoBean
    private OrcamentoRepository orcamentoRepository;

    @Captor
    ArgumentCaptor<OrcamentoTradicionalEntity> orcamentoTradicionalCaptor;

    private OrcamentoTradicionalDto orcamentoTradicionalDto;
    private OrcamentoTradicionalEntity orcamentoTradicional;
    private Page<OrcamentoTradicionalEntity> pageOrcamentos;

    @BeforeEach
    void setUp() {
        orcamentoTradicionalDto = OrcamentoTradicionalDto.builder()
                .id("id-teste")
                .cliente("Cliente teste")
                .cnpjCpf("cnpj/cpf teste")
                .produtos(List.of(ProdutoOrcamentoDto.builder().descricao("descricao-teste").quantidade(1).valor(new BigDecimal(200)).build(), ProdutoOrcamentoDto.builder().descricao("descricao-teste-2").quantidade(1).valor(new BigDecimal(200)).build()))
                .observacoes("Observações teste")
                .camposPersonalizados(List.of(CampoPersonalizadoDto.builder().titulo("Titulo teste").valor("Valor teste").build(), CampoPersonalizadoDto.builder().titulo("Titulo teste 2").valor("Valor teste 2").build()))
                .urlArquivo("url teste")
                .idUsuario("id-usuario-test")
                .valorTotal(BigDecimal.valueOf(100))
                .tipoOrcamento(TipoOrcamento.TRADICIONAL)
                .status(StatusOrcamento.PENDENTE)
                .dataCriacao(LocalDate.now())
                .build();

        orcamentoTradicional = OrcamentoTradicionalEntity.builder()
                .id("id-teste")
                .cliente("Cliente teste 2")
                .cnpjCpf("cnpj/cpf teste 2")
                .produtos(List.of(ProdutoOrcamentoEntity.builder().descricao("descricao-teste").quantidade(1).valor(new BigDecimal(200)).build(), ProdutoOrcamentoEntity.builder().descricao("descricao-teste-2").quantidade(1).valor(new BigDecimal(200)).build()))
                .observacoes("Observações teste")
                .camposPersonalizados(List.of(CampoPersonalizadoEntity.builder().titulo("Titulo teste").valor("Valor teste").build(), CampoPersonalizadoEntity.builder().titulo("Titulo teste 2").valor("Valor teste 2").build()))
                .urlArquivo("url teste 2")
                .idUsuario("id-usuario-test-2")
                .valorTotal(BigDecimal.valueOf(100))
                .tipoOrcamento(TipoOrcamento.TRADICIONAL)
                .status(StatusOrcamento.PENDENTE)
                .dataCriacao(LocalDate.now())
                .build();

        pageOrcamentos = new PageImpl<>(List.of(
                OrcamentoTradicionalEntity.builder()
                        .id("id-teste")
                        .cliente("Cliente teste 2")
                        .cnpjCpf("cnpj/cpf teste 2")
                        .produtos(List.of(ProdutoOrcamentoEntity.builder().descricao("descricao-teste").quantidade(1).valor(new BigDecimal(200)).build(), ProdutoOrcamentoEntity.builder().descricao("descricao-teste-2").quantidade(1).valor(new BigDecimal(200)).build()))
                        .observacoes("Observações teste")
                        .camposPersonalizados(List.of(CampoPersonalizadoEntity.builder().titulo("Titulo teste").valor("Valor teste").build(), CampoPersonalizadoEntity.builder().titulo("Titulo teste 2").valor("Valor teste 2").build()))
                        .urlArquivo("url teste 2")
                        .idUsuario("id-usuario-test-2")
                        .valorTotal(BigDecimal.valueOf(100))
                        .tipoOrcamento(TipoOrcamento.TRADICIONAL)
                        .status(StatusOrcamento.PENDENTE)
                        .dataCriacao(LocalDate.now())
                        .build(),
                OrcamentoTradicionalEntity.builder()
                        .id("id-teste")
                        .cliente("Cliente teste 2")
                        .cnpjCpf("cnpj/cpf teste 2")
                        .produtos(List.of(ProdutoOrcamentoEntity.builder().descricao("descricao-teste").quantidade(1).valor(new BigDecimal(200)).build(), ProdutoOrcamentoEntity.builder().descricao("descricao-teste-2").quantidade(1).valor(new BigDecimal(200)).build()))
                        .observacoes("Observações teste")
                        .camposPersonalizados(List.of(CampoPersonalizadoEntity.builder().titulo("Titulo teste").valor("Valor teste").build(), CampoPersonalizadoEntity.builder().titulo("Titulo teste 2").valor("Valor teste 2").build()))
                        .urlArquivo("url teste 2")
                        .idUsuario("id-usuario-test-2")
                        .valorTotal(BigDecimal.valueOf(100))
                        .tipoOrcamento(TipoOrcamento.TRADICIONAL)
                        .status(StatusOrcamento.PENDENTE)
                        .dataCriacao(LocalDate.now())
                        .build(),
                OrcamentoTradicionalEntity.builder()
                        .id("id-teste")
                        .cliente("Cliente teste 2")
                        .cnpjCpf("cnpj/cpf teste 2")
                        .produtos(List.of(ProdutoOrcamentoEntity.builder().descricao("descricao-teste").quantidade(1).valor(new BigDecimal(200)).build(), ProdutoOrcamentoEntity.builder().descricao("descricao-teste-2").quantidade(1).valor(new BigDecimal(200)).build()))
                        .observacoes("Observações teste")
                        .camposPersonalizados(List.of(CampoPersonalizadoEntity.builder().titulo("Titulo teste").valor("Valor teste").build(), CampoPersonalizadoEntity.builder().titulo("Titulo teste 2").valor("Valor teste 2").build()))
                        .urlArquivo("url teste 2")
                        .idUsuario("id-usuario-test-2")
                        .valorTotal(BigDecimal.valueOf(100))
                        .tipoOrcamento(TipoOrcamento.TRADICIONAL)
                        .status(StatusOrcamento.PENDENTE)
                        .dataCriacao(LocalDate.now())
                        .build()
        ));

    }

    @Test
    void deveCriarOrcamentoComSucesso() throws Exception {
        UsuarioEntity usuario = UsuarioEntity.builder()
                .id(UUID.randomUUID().toString())
                .email("emailtste@gmail.com")
                .plano(Plano.PLUS)
                .quantidadeOrcamentos(0)
                .build();


        orcamentoTradicionalDto.setId(null);
        Mockito.when(orcamentoTradicionalRepository.save(orcamentoTradicionalCaptor.capture())).thenReturn(orcamentoTradicional);
        Mockito.when(usuarioRepository.findByEmail("emailtste@gmail.com"))
                .thenReturn(Optional.of(usuario));
        Mockito.when(usuarioRepository.findById(any())).thenReturn(Optional.of(usuario));
        Mockito.when(orcamentoTradicionalRepository.findByIdUsuario(anyString(), any())).thenReturn(pageOrcamentos);
        Mockito.when(orcamentoRepository.findByIdUsuario(anyString(), any())).thenReturn(new PageImpl<>(List.of()));
        Mockito.when(usuarioRepository.save(Mockito.any())).thenReturn(usuario);

        mockMvc.perform(post("/orcamentos/tradicionais")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orcamentoTradicionalDto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/orcamentos/tradicionais/" + orcamentoTradicional.getId()))
                .andExpect(jsonPath("$.dado.cnpjCpf").value(orcamentoTradicional.getCnpjCpf()));

        Assertions.assertEquals(orcamentoTradicionalDto.getCnpjCpf(), orcamentoTradicionalCaptor.getValue().getCnpjCpf());
        Assertions.assertEquals(TipoOrcamento.TRADICIONAL, orcamentoTradicionalCaptor.getValue().getTipoOrcamento());

        Mockito.verify(orcamentoTradicionalRepository).save(any());
    }

    @Test
    void deveConsultarPorIdComSucesso() throws Exception {
        Mockito.when(orcamentoTradicionalRepository.findById(anyString())).thenReturn(Optional.of(orcamentoTradicional));

        mockMvc.perform(get("/orcamentos/tradicionais/" + orcamentoTradicionalDto.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dado.id").value(orcamentoTradicionalDto.getId()));

        Mockito.verify(orcamentoTradicionalRepository).findById(anyString());
    }

    @Test
    void deveListarPorUsuarioComSucesso() throws Exception {
        Mockito.when(orcamentoTradicionalRepository.findByIdUsuario(anyString(), any())).thenReturn(pageOrcamentos);

        mockMvc.perform(get("/orcamentos/tradicionais/usuario/idUsuarioTeste")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "dataCriacao,desc")
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.dado.number")
                        .value(0))
                .andExpect(jsonPath("$.dado.numberOfElements")
                        .value(pageOrcamentos.getTotalElements()));

        Mockito.verify(orcamentoTradicionalRepository).findByIdUsuario(anyString(), any());
    }

    @Test
    void deveAlterarComSucesso() throws Exception {
        orcamentoTradicionalDto.setCliente("Cliente novo teste");

        Mockito.when(orcamentoTradicionalRepository.findById(anyString())).thenReturn(Optional.of(orcamentoTradicional));
        Mockito.when(orcamentoTradicionalRepository.save(orcamentoTradicionalCaptor.capture())).thenReturn(orcamentoTradicional);

        mockMvc.perform(put("/orcamentos/tradicionais/" + orcamentoTradicionalDto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orcamentoTradicionalDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dado.id").value(orcamentoTradicional.getId()));

        Assertions.assertEquals(orcamentoTradicionalDto.getCliente(), orcamentoTradicionalCaptor.getValue().getCliente());

        Mockito.verify(orcamentoTradicionalRepository).findById(anyString());
        Mockito.verify(orcamentoTradicionalRepository).save(any());
    }

    @Test
    void deveDeletarComSucesso() throws Exception {

        Mockito.when(orcamentoTradicionalRepository.findById(anyString())).thenReturn(Optional.of(orcamentoTradicional));
        Mockito.doNothing().when(orcamentoTradicionalRepository).deleteById(anyString());

        mockMvc.perform(delete("/orcamentos/tradicionais/" + orcamentoTradicionalDto.getId()))
                .andExpect(status().isNoContent());

        Mockito.verify(orcamentoTradicionalRepository).findById(any());
        Mockito.verify(orcamentoTradicionalRepository).deleteById(anyString());
    }
}