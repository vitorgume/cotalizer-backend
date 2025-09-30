package com.gumeinteligenciacomercial.orcaja.entrypoint.mapper;

import com.gumeinteligenciacomercial.orcaja.domain.Template;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.TemplateDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TemplateMapperTest {

    private Template templateDomain;
    private TemplateDto templateDto;

    @BeforeEach
    void setUp() {
        templateDomain = Template.builder()
                .id("teste D")
                .nomeArquivo("teste D")
                .build();

        templateDto = TemplateDto.builder()
                .id("teste O")
                .nomeArquivo("teste O")
                .build();
    }

    @Test
    void deveRetornarDomainComSucesso() {
        Template resultado = TemplateMapper.paraDomain(templateDto);

        Assertions.assertEquals(resultado.getId(), templateDto.getId());
        Assertions.assertEquals(resultado.getNomeArquivo(), templateDto.getNomeArquivo());
    }

    @Test
    void deveRetornarDtoComSucesso() {
        TemplateDto resultado = TemplateMapper.paraDto(templateDomain);

        Assertions.assertEquals(resultado.getId(), templateDomain.getId());
        Assertions.assertEquals(resultado.getNomeArquivo(), templateDomain.getNomeArquivo());
    }
}