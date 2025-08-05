package com.gumeinteligenciacomercial.orcaja.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrcamentoTradicionalTest {

    @Test
    void deveAlterarDadosDeOrcamento() {
        OrcamentoTradicional novosDados = OrcamentoTradicional.builder()
                .id("Id teste")
                .cliente("Cliente teste")
                .cnpjCpf("12345678987")
                .produtos(List.of(new ProdutoOrcamento("Descricao 1", BigDecimal.valueOf(30L), 1), new ProdutoOrcamento("Descricao 2", BigDecimal.valueOf(40L), 2)))
                .observacoes("Observações teste")
                .camposPersonalizados(List.of(new CampoPersonalizado("Titulo 1", "Valor 1"), new CampoPersonalizado("Titulo 2", "Valor 2")))
                .urlArquivo("Url arquivo")
                .idUsuario("Id usuario teste")
                .valorTotal(BigDecimal.valueOf(10))
                .tipoOrcamento(TipoOrcamento.TRADICIONAL)
                .status(StatusOrcamento.REPROVADO)
                .dataCriacao(LocalDate.now())
                .build();

        OrcamentoTradicional orcamentoTradicional = OrcamentoTradicional.builder()
                .id("Id teste 2")
                .cliente("Cliente teste 2")
                .cnpjCpf("12345678988")
                .produtos(List.of(new ProdutoOrcamento("Descricao teste 1", BigDecimal.valueOf(30L), 1), new ProdutoOrcamento("Descricao teste 2", BigDecimal.valueOf(40L), 2)))
                .observacoes("Observações teste 2")
                .camposPersonalizados(List.of(new CampoPersonalizado("Titulo teste 1", "Valor teste 1"), new CampoPersonalizado("Titulo teste 2", "Valor teste 2")))
                .urlArquivo("Url arquivo 2")
                .idUsuario("Id usuario teste 2")
                .valorTotal(BigDecimal.valueOf(10))
                .tipoOrcamento(TipoOrcamento.TRADICIONAL)
                .status(StatusOrcamento.REPROVADO)
                .dataCriacao(LocalDate.now())
                .build();

        orcamentoTradicional.setDados(novosDados);

        Assertions.assertEquals(orcamentoTradicional.getCnpjCpf(), novosDados.getCnpjCpf());
        Assertions.assertEquals(orcamentoTradicional.getProdutos(), novosDados.getProdutos());
        Assertions.assertEquals(orcamentoTradicional.getObservacoes(), novosDados.getObservacoes());
        Assertions.assertEquals(orcamentoTradicional.getCamposPersonalizados(), novosDados.getCamposPersonalizados());
        Assertions.assertEquals(orcamentoTradicional.getUrlArquivo(), novosDados.getUrlArquivo());
        Assertions.assertEquals(orcamentoTradicional.getIdUsuario(), novosDados.getIdUsuario());
        Assertions.assertEquals(orcamentoTradicional.getValorTotal(), novosDados.getValorTotal());
        Assertions.assertEquals(orcamentoTradicional.getStatus(), novosDados.getStatus());
    }
}