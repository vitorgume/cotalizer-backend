package com.gumeinteligenciacomercial.orcaja.infrastructure.mapper;

import com.gumeinteligenciacomercial.orcaja.domain.Template;
import com.gumeinteligenciacomercial.orcaja.infrastructure.repositories.entities.TemplateEntity;

public class TemplateMapper {

    public static Template paraDomain(TemplateEntity entity) {
        return Template.builder()
                .id(entity.getId())
                .nomeArquivo(entity.getNomeArquivo())
                .build();
    }

    public static TemplateEntity paraEntity(Template domain) {
        return TemplateEntity.builder()
                .id(domain.getId())
                .nomeArquivo(domain.getNomeArquivo())
                .build();
    }
}
