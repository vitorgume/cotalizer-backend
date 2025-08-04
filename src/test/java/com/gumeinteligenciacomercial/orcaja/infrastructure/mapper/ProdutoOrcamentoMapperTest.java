package com.gumeinteligenciacomercial.orcaja.infrastructure.mapper;

import com.gumeinteligenciacomercial.orcaja.domain.ProdutoOrcamento;
import com.gumeinteligenciacomercial.orcaja.infrastructure.repositories.entities.ProdutoOrcamentoEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ProdutoOrcamentoMapperTest {

    private ProdutoOrcamento produtoOrcamentoDomain;
    private ProdutoOrcamentoEntity produtoOrcamentoEntity;

    @BeforeEach
    void setUp() {
        produtoOrcamentoDomain = ProdutoOrcamento.builder()
                .descricao("Descrição teste")
                .valor(BigDecimal.valueOf(200))
                .quantidade(1)
                .build();

        produtoOrcamentoEntity = ProdutoOrcamentoEntity.builder()
                .descricao("Descrição teste 2")
                .valor(BigDecimal.valueOf(300))
                .quantidade(5)
                .build();
    }

    @Test
    void deveTransformarParaDomain() {
        ProdutoOrcamento produtoOrcamentoTeste = ProdutoOrcamentoMapper.paraDomain(produtoOrcamentoEntity);

        Assertions.assertEquals(produtoOrcamentoEntity.getDescricao(), produtoOrcamentoTeste.getDescricao());
        Assertions.assertEquals(produtoOrcamentoEntity.getValor(), produtoOrcamentoTeste.getValor());
        Assertions.assertEquals(produtoOrcamentoEntity.getQuantidade(), produtoOrcamentoTeste.getQuantidade());
    }

    @Test
    void deveTransformarParaEntity() {
        ProdutoOrcamentoEntity produtoOrcamentoTeste = ProdutoOrcamentoMapper.paraEntity(produtoOrcamentoDomain);

        Assertions.assertEquals(produtoOrcamentoDomain.getDescricao(), produtoOrcamentoTeste.getDescricao());
        Assertions.assertEquals(produtoOrcamentoDomain.getValor(), produtoOrcamentoTeste.getValor());
        Assertions.assertEquals(produtoOrcamentoDomain.getQuantidade(), produtoOrcamentoTeste.getQuantidade());
    }
}