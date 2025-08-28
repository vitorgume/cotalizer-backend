package com.gumeinteligenciacomercial.orcaja.entrypoint.mapper;

import com.gumeinteligenciacomercial.orcaja.domain.*;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.CampoPersonalizadoDto;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.OrcamentoDto;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.OrcamentoTradicionalDto;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.ProdutoOrcamentoDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrcamentoTradicionalMapperTest {

    private OrcamentoTradicional orcamentoTradicionalDomain;
    private OrcamentoTradicionalDto orcamentoTradicionalDto;
    private Page<OrcamentoTradicional> orcamentosTradicionais;

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
                .build();

        orcamentoTradicionalDto = OrcamentoTradicionalDto.builder()
                .id("id-teste")
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

        orcamentosTradicionais = new PageImpl<>(List.of(
                OrcamentoTradicional.builder()
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
                        .build(),
                OrcamentoTradicional.builder()
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
                        .build(),
                OrcamentoTradicional.builder()
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
                        .build()
        ));
    }

    @Test
    void deveRetornarDto() {
        OrcamentoTradicionalDto orcamentoTradicionalTeste = OrcamentoTradicionalMapper.paraDto(orcamentoTradicionalDomain);

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
        Assertions.assertEquals(orcamentoTradicionalDomain.getDataCriacao(), orcamentoTradicionalTeste.getDataCriacao());
    }

    @Test
    void deveRetornarDomain() {
        OrcamentoTradicional orcamentoTradicionalTeste = OrcamentoTradicionalMapper.paraDomain(orcamentoTradicionalDto);

        Assertions.assertEquals(orcamentoTradicionalDto.getId(), orcamentoTradicionalTeste.getId());
        Assertions.assertEquals(orcamentoTradicionalDto.getCliente(), orcamentoTradicionalTeste.getCliente());
        Assertions.assertEquals(orcamentoTradicionalDto.getCnpjCpf(), orcamentoTradicionalTeste.getCnpjCpf());
        Assertions.assertEquals(orcamentoTradicionalDto.getProdutos().getFirst().getDescricao(), orcamentoTradicionalTeste.getProdutos().getFirst().getDescricao());
        Assertions.assertEquals(orcamentoTradicionalDto.getProdutos().get(1).getDescricao(), orcamentoTradicionalTeste.getProdutos().get(1).getDescricao());
        Assertions.assertEquals(orcamentoTradicionalDto.getObservacoes(), orcamentoTradicionalTeste.getObservacoes());
        Assertions.assertEquals(orcamentoTradicionalDto.getCamposPersonalizados().getFirst().getTitulo(), orcamentoTradicionalTeste.getCamposPersonalizados().getFirst().getTitulo());
        Assertions.assertEquals(orcamentoTradicionalDto.getCamposPersonalizados().get(1).getTitulo(), orcamentoTradicionalTeste.getCamposPersonalizados().get(1).getTitulo());
        Assertions.assertEquals(orcamentoTradicionalDto.getUrlArquivo(), orcamentoTradicionalTeste.getUrlArquivo());
        Assertions.assertEquals(orcamentoTradicionalDto.getIdUsuario(), orcamentoTradicionalTeste.getIdUsuario());
        Assertions.assertEquals(orcamentoTradicionalDto.getValorTotal(), orcamentoTradicionalTeste.getValorTotal());
        Assertions.assertEquals(orcamentoTradicionalDto.getTipoOrcamento(), orcamentoTradicionalTeste.getTipoOrcamento());
        Assertions.assertEquals(orcamentoTradicionalDto.getStatus(), orcamentoTradicionalTeste.getStatus());
        Assertions.assertEquals(orcamentoTradicionalDto.getDataCriacao(), orcamentoTradicionalTeste.getDataCriacao());
    }

    @Test
    void deveRetornarPageDeDtos() {
        Page<OrcamentoTradicionalDto> orcamentosTradicionaisResult = OrcamentoTradicionalMapper.paraDtos(orcamentosTradicionais);

        for (int i = 0; i < orcamentosTradicionaisResult.getContent().size(); i++) {
            this.validaOrcamentoTradicionalDto(orcamentosTradicionaisResult.getContent().get(i), i);
        }
    }

    private void validaOrcamentoTradicionalDto(OrcamentoTradicionalDto orcamentoTradicionalTeste, int index) {
        Assertions.assertEquals(orcamentosTradicionais.getContent().get(index).getId(), orcamentoTradicionalTeste.getId());
        Assertions.assertEquals(orcamentosTradicionais.getContent().get(index).getCliente(), orcamentoTradicionalTeste.getCliente());
        Assertions.assertEquals(orcamentosTradicionais.getContent().get(index).getCnpjCpf(), orcamentoTradicionalTeste.getCnpjCpf());
        Assertions.assertEquals(orcamentosTradicionais.getContent().get(index).getProdutos().getFirst().getDescricao(), orcamentoTradicionalTeste.getProdutos().getFirst().getDescricao());
        Assertions.assertEquals(orcamentosTradicionais.getContent().get(index).getProdutos().get(1).getDescricao(), orcamentoTradicionalTeste.getProdutos().get(1).getDescricao());
        Assertions.assertEquals(orcamentosTradicionais.getContent().get(index).getObservacoes(), orcamentoTradicionalTeste.getObservacoes());
        Assertions.assertEquals(orcamentosTradicionais.getContent().get(index).getCamposPersonalizados().getFirst().getTitulo(), orcamentoTradicionalTeste.getCamposPersonalizados().getFirst().getTitulo());
        Assertions.assertEquals(orcamentosTradicionais.getContent().get(index).getCamposPersonalizados().get(1).getTitulo(), orcamentoTradicionalTeste.getCamposPersonalizados().get(1).getTitulo());
        Assertions.assertEquals(orcamentosTradicionais.getContent().get(index).getUrlArquivo(), orcamentoTradicionalTeste.getUrlArquivo());
        Assertions.assertEquals(orcamentosTradicionais.getContent().get(index).getIdUsuario(), orcamentoTradicionalTeste.getIdUsuario());
        Assertions.assertEquals(orcamentosTradicionais.getContent().get(index).getValorTotal(), orcamentoTradicionalTeste.getValorTotal());
        Assertions.assertEquals(orcamentosTradicionais.getContent().get(index).getTipoOrcamento(), orcamentoTradicionalTeste.getTipoOrcamento());
        Assertions.assertEquals(orcamentosTradicionais.getContent().get(index).getStatus(), orcamentoTradicionalTeste.getStatus());
        Assertions.assertEquals(orcamentosTradicionais.getContent().get(index).getDataCriacao(), orcamentoTradicionalTeste.getDataCriacao());
    }
}