package com.gumeinteligenciacomercial.orcaja.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class OrcamentoTest {

    @Test
    void deveAlterarDadosDeOrcamento() {
        Map<String, Object> novoOrcamentoFormatado = Map.of(
                "cliente", "Cliente teste",
                "desconto", 5
        );

        Map<String, Object> orcamentoFormatado = Map.of(
                "cliente", "Cliente",
                "desconto", 3
        );

        Orcamento novosDados = Orcamento.builder()
                .orcamentoFormatado(novoOrcamentoFormatado)
                .titulo("Novo titulo")
                .urlArquivo("urlteste")
                .status(StatusOrcamento.REPROVADO)
                .build();

        Orcamento orcamento = Orcamento.builder()
                .orcamentoFormatado(orcamentoFormatado)
                .titulo("Titulo")
                .urlArquivo("urltesteantigo")
                .status(StatusOrcamento.PENDENTE)
                .build();

        orcamento.setDados(novosDados);

        Assertions.assertEquals(orcamento.getOrcamentoFormatado(), novosDados.getOrcamentoFormatado());
        Assertions.assertEquals(orcamento.getTitulo(), novosDados.getTitulo());
        Assertions.assertEquals(orcamento.getUrlArquivo(), novosDados.getUrlArquivo());
        Assertions.assertEquals(orcamento.getStatus(), novosDados.getStatus());
    }
}