package com.gumeinteligenciacomercial.orcaja.infrastructure.mapper;

import com.gumeinteligenciacomercial.orcaja.domain.Plano;
import com.gumeinteligenciacomercial.orcaja.infrastructure.repositories.entities.PlanoEntity;

public class PlanoMapper {

    public static Plano paraDomain(PlanoEntity entity) {
        return Plano.builder()
                .id(entity.getId())
                .titulo(entity.getTitulo())
                .valor(entity.getValor())
                .limite(entity.getLimite())
                .padrao(entity.getPadrao())
                .idPlanoStripe(entity.getIdPlanoStripe())
                .grau(entity.getGrau())
                .build();
    }
}
