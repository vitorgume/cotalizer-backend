package com.gumeinteligenciacomercial.orcaja.infrastructure.mapper;

import com.gumeinteligenciacomercial.orcaja.domain.Prompt;
import com.gumeinteligenciacomercial.orcaja.infrastructure.repositories.entities.PromptEntity;

public class PromptMapper {

    public static Prompt paraDomain(PromptEntity entity) {
        return Prompt.builder()
                .id(entity.getId())
                .conteudo(entity.getConteudo())
                .ativo(entity.getAtivo())
                .modelIa(entity.getModelIa())
                .build();
    }

    public static PromptEntity paraEntity(Prompt domain) {
        return PromptEntity.builder()
                .id(domain.getId())
                .conteudo(domain.getConteudo())
                .ativo(domain.getAtivo())
                .modelIa(domain.getModelIa())
                .build();
    }
}
