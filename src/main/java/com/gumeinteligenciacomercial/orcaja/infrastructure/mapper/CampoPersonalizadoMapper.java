package com.gumeinteligenciacomercial.orcaja.infrastructure.mapper;

import com.gumeinteligenciacomercial.orcaja.domain.CampoPersonalizado;
import com.gumeinteligenciacomercial.orcaja.infrastructure.repositories.entities.CampoPersonalizadoEntity;

public class CampoPersonalizadoMapper {

    public static CampoPersonalizado paraDomain(CampoPersonalizadoEntity entity) {
        return CampoPersonalizado.builder()
                .titulo(entity.getTitulo())
                .valor(entity.getValor())
                .build();
    }

    public static CampoPersonalizadoEntity paraEntity(CampoPersonalizado domain) {
        return CampoPersonalizadoEntity.builder()
                .titulo(domain.getTitulo())
                .valor(domain.getValor())
                .build();
    }
}
