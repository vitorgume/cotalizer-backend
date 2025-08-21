package com.gumeinteligenciacomercial.orcaja.infrastructure.mapper;

import com.gumeinteligenciacomercial.orcaja.domain.Prompt;
import com.gumeinteligenciacomercial.orcaja.infrastructure.repositories.entities.PromptEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PromptMapperTest {

    private Prompt promptDomain;
    private PromptEntity promptEntity;

    @BeforeEach
    void setUp() {
        promptDomain = Prompt.builder()
                .id("id-teste")
                .modelIa("modelo teste")
                .conteudo("Conteudo teste")
                .ativo(true)
                .build();

        promptEntity = PromptEntity.builder()
                .id("id-teste-2")
                .modelIa("modelo teste 2")
                .conteudo("Conteudo teste 2")
                .ativo(false)
                .build();
    }

    @Test
    void deveTransformarParaDomain() {
        Prompt promptTeste = PromptMapper.paraDomain(promptEntity);

        Assertions.assertEquals(promptEntity.getId(), promptTeste.getId());
        Assertions.assertEquals(promptEntity.getModelIa(), promptTeste.getModelIa());
        Assertions.assertEquals(promptEntity.getConteudo(), promptTeste.getConteudo());
        Assertions.assertEquals(promptEntity.getAtivo(), promptTeste.getAtivo());
    }

    @Test
    void deveRetornarParaEntity() {
        PromptEntity promptTeste = PromptMapper.paraEntity(promptDomain);

        Assertions.assertEquals(promptDomain.getId(), promptTeste.getId());
        Assertions.assertEquals(promptDomain.getModelIa(), promptTeste.getModelIa());
        Assertions.assertEquals(promptDomain.getConteudo(), promptTeste.getConteudo());
        Assertions.assertEquals(promptDomain.getAtivo(), promptTeste.getAtivo());
    }
}