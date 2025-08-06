package com.gumeinteligenciacomercial.orcaja.entrypoint.mapper;

import com.gumeinteligenciacomercial.orcaja.domain.ProdutoOrcamento;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.ProdutoOrcamentoDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ProdutoOrcamentoMapperTest {

    private ProdutoOrcamento produtoOrcamentoDomain;
    private ProdutoOrcamentoDto produtoOrcamentoDto;

    @BeforeEach
    void setUp() {
        produtoOrcamentoDomain = ProdutoOrcamento.builder()
                .descricao("Descrição teste")
                .valor(BigDecimal.valueOf(20L))
                .quantidade(3)
                .build();

        produtoOrcamentoDto = ProdutoOrcamentoDto.builder()
                .descricao("Descrição teste 2")
                .valor(BigDecimal.valueOf(30L))
                .quantidade(4)
                .build();
    }

    @Test
    void deveRetornarDto() {
        ProdutoOrcamentoDto produtoOrcamentoTeste = ProdutoOrcamentoMapper.paraDto(produtoOrcamentoDomain);

        Assertions.assertEquals(produtoOrcamentoDomain.getDescricao(), produtoOrcamentoTeste.getDescricao());
        Assertions.assertEquals(produtoOrcamentoDomain.getValor(), produtoOrcamentoTeste.getValor());
        Assertions.assertEquals(produtoOrcamentoDomain.getQuantidade(), produtoOrcamentoTeste.getQuantidade());
    }

    @Test
    void deveRetornarDomain() {
        ProdutoOrcamento produtoOrcamentoTeste = ProdutoOrcamentoMapper.paraDomain(produtoOrcamentoDto);

        Assertions.assertEquals(produtoOrcamentoDto.getDescricao(), produtoOrcamentoTeste.getDescricao());
        Assertions.assertEquals(produtoOrcamentoDto.getValor(), produtoOrcamentoTeste.getValor());
        Assertions.assertEquals(produtoOrcamentoDto.getQuantidade(), produtoOrcamentoTeste.getQuantidade());
    }
}