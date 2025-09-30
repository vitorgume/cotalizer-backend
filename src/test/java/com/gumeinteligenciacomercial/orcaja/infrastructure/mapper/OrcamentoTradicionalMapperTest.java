package com.gumeinteligenciacomercial.orcaja.infrastructure.mapper;

import com.gumeinteligenciacomercial.orcaja.domain.*;
import com.gumeinteligenciacomercial.orcaja.infrastructure.repositories.entities.CampoPersonalizadoEntity;
import com.gumeinteligenciacomercial.orcaja.infrastructure.repositories.entities.OrcamentoTradicionalEntity;
import com.gumeinteligenciacomercial.orcaja.infrastructure.repositories.entities.ProdutoOrcamentoEntity;
import com.gumeinteligenciacomercial.orcaja.infrastructure.repositories.entities.TemplateEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OrcamentoTradicionalMapperTest {

    private OrcamentoTradicional orcamentoTradicionalDomain;
    private OrcamentoTradicionalEntity orcamentoTradicionalEntity;

    @BeforeEach
    void setUp() {
        orcamentoTradicionalDomain = OrcamentoTradicional.builder()
                .id("id-teste")
                .cliente("Cliente teste")
                .cnpjCpf("cnpj/cpf teste")
                .produtos(List.of(ProdutoOrcamento.builder().descricao("descricao-teste").build(), ProdutoOrcamento.builder().descricao("descricao-teste-2").build()))
                .observacoes("Observações teste")
                .camposPersonalizados(List.of(CampoPersonalizado.builder().titulo("Titulo teste").valor("Valor teste").build(), CampoPersonalizado.builder().titulo("Titulo teste 2").valor("Valor teste 2").build()))
                .urlArquivo("url teste")
                .idUsuario("id-usuario-test")
                .valorTotal(BigDecimal.valueOf(100))
                .tipoOrcamento(TipoOrcamento.TRADICIONAL)
                .status(StatusOrcamento.PENDENTE)
                .dataCriacao(LocalDate.now())
                .template(Template.builder().id("teste").nomeArquivo("teste").build())
                .build();

        orcamentoTradicionalEntity = OrcamentoTradicionalEntity.builder()
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
                .template(TemplateEntity.builder().id("teste").nomeArquivo("teste").build())
                .build();
    }


    @Test
    void deveRetornarDomain() {
        OrcamentoTradicional orcamentoTradicionalTeste = OrcamentoTradicionalMapper.paraDomain(orcamentoTradicionalEntity);

        Assertions.assertEquals(orcamentoTradicionalEntity.getId(), orcamentoTradicionalTeste.getId());
        Assertions.assertEquals(orcamentoTradicionalEntity.getCliente(), orcamentoTradicionalTeste.getCliente());
        Assertions.assertEquals(orcamentoTradicionalEntity.getCnpjCpf(), orcamentoTradicionalTeste.getCnpjCpf());
        Assertions.assertEquals(orcamentoTradicionalEntity.getProdutos().getFirst().getDescricao(), orcamentoTradicionalTeste.getProdutos().getFirst().getDescricao());
        Assertions.assertEquals(orcamentoTradicionalEntity.getProdutos().get(1).getDescricao(), orcamentoTradicionalTeste.getProdutos().get(1).getDescricao());
        Assertions.assertEquals(orcamentoTradicionalEntity.getObservacoes(), orcamentoTradicionalTeste.getObservacoes());
        Assertions.assertEquals(orcamentoTradicionalEntity.getCamposPersonalizados().getFirst().getTitulo(), orcamentoTradicionalTeste.getCamposPersonalizados().getFirst().getTitulo());
        Assertions.assertEquals(orcamentoTradicionalEntity.getCamposPersonalizados().get(1).getTitulo(), orcamentoTradicionalTeste.getCamposPersonalizados().get(1).getTitulo());
        Assertions.assertEquals(orcamentoTradicionalEntity.getUrlArquivo(), orcamentoTradicionalTeste.getUrlArquivo());
        Assertions.assertEquals(orcamentoTradicionalEntity.getIdUsuario(), orcamentoTradicionalTeste.getIdUsuario());
        Assertions.assertEquals(orcamentoTradicionalEntity.getValorTotal(), orcamentoTradicionalTeste.getValorTotal());
        Assertions.assertEquals(orcamentoTradicionalEntity.getTipoOrcamento(), orcamentoTradicionalTeste.getTipoOrcamento());
        Assertions.assertEquals(orcamentoTradicionalEntity.getStatus(), orcamentoTradicionalTeste.getStatus());
        Assertions.assertEquals(orcamentoTradicionalEntity.getTemplate().getId(), orcamentoTradicionalTeste.getTemplate().getId());
        Assertions.assertEquals(orcamentoTradicionalEntity.getDataCriacao(), orcamentoTradicionalTeste.getDataCriacao());
    }

    @Test
    void deveTransformarParaEntity() {
        OrcamentoTradicionalEntity orcamentoTradicionalTeste = OrcamentoTradicionalMapper.paraEntity(orcamentoTradicionalDomain);

        Assertions.assertEquals(orcamentoTradicionalDomain.getId(), orcamentoTradicionalTeste.getId());
        Assertions.assertEquals(orcamentoTradicionalDomain.getCliente(), orcamentoTradicionalTeste.getCliente());
        Assertions.assertEquals(orcamentoTradicionalDomain.getCnpjCpf(), orcamentoTradicionalTeste.getCnpjCpf());
        Assertions.assertEquals(orcamentoTradicionalDomain.getProdutos().getFirst().getDescricao(), orcamentoTradicionalTeste.getProdutos().getFirst().getDescricao());
        Assertions.assertEquals(orcamentoTradicionalDomain.getProdutos().get(1).getDescricao(), orcamentoTradicionalTeste.getProdutos().get(1).getDescricao());
        Assertions.assertEquals(orcamentoTradicionalDomain.getObservacoes(), orcamentoTradicionalTeste.getObservacoes());
        Assertions.assertEquals(orcamentoTradicionalDomain.getCamposPersonalizados().getFirst().getTitulo(), orcamentoTradicionalTeste.getCamposPersonalizados().getFirst().getTitulo());
        Assertions.assertEquals(orcamentoTradicionalDomain.getCamposPersonalizados().get(1).getTitulo(), orcamentoTradicionalTeste.getCamposPersonalizados().get(1).getTitulo());
        Assertions.assertEquals(orcamentoTradicionalDomain.getUrlArquivo(), orcamentoTradicionalTeste.getUrlArquivo());
        Assertions.assertEquals(orcamentoTradicionalDomain.getIdUsuario(), orcamentoTradicionalTeste.getIdUsuario());
        Assertions.assertEquals(orcamentoTradicionalDomain.getValorTotal(), orcamentoTradicionalTeste.getValorTotal());
        Assertions.assertEquals(orcamentoTradicionalDomain.getTipoOrcamento(), orcamentoTradicionalTeste.getTipoOrcamento());
        Assertions.assertEquals(orcamentoTradicionalDomain.getStatus(), orcamentoTradicionalTeste.getStatus());
        Assertions.assertEquals(orcamentoTradicionalDomain.getTemplate().getId(), orcamentoTradicionalTeste.getTemplate().getId());
        Assertions.assertEquals(orcamentoTradicionalDomain.getDataCriacao(), orcamentoTradicionalTeste.getDataCriacao());
    }
}