package com.gumeinteligenciacomercial.orcaja.infrastructure.mapper;

import com.gumeinteligenciacomercial.orcaja.domain.Template;
import com.gumeinteligenciacomercial.orcaja.infrastructure.repositories.entities.TemplateEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TemplateMapperTest {

    private Template templateDomain;
    private TemplateEntity templateEntity;

    @BeforeEach
    void setUp() {
        templateDomain = Template.builder()
                .id("teste D")
                .nomeArquivo("teste D")
                .build();

        templateEntity = TemplateEntity.builder()
                .id("teste E")
                .nomeArquivo("teste E")
                .build();
    }

    @Test
    void deveRetornarDomainComSucesso() {
        Template resultado = TemplateMapper.paraDomain(templateEntity);

        Assertions.assertEquals(resultado.getId(), templateEntity.getId());
        Assertions.assertEquals(resultado.getNomeArquivo(), templateEntity.getNomeArquivo());
    }

    @Test
    void deveRetornarEntityComSucesso() {
        TemplateEntity resultado = TemplateMapper.paraEntity(templateDomain);

        Assertions.assertEquals(resultado.getId(), templateDomain.getId());
        Assertions.assertEquals(resultado.getNomeArquivo(), templateDomain.getNomeArquivo());
    }
}