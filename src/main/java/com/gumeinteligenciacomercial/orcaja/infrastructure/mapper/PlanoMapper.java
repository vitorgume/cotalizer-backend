package com.gumeinteligenciacomercial.orcaja.infrastructure.mapper;

import com.gumeinteligenciacomercial.orcaja.domain.Plano;
import com.gumeinteligenciacomercial.orcaja.infrastructure.repositories.entities.PlanoEntity;

public class PlanoMapper {

    public static Plano paraDomain(PlanoEntity entity) {
        return Plano.builder()
                .id(entity.getId())
                .titulo(entity.getTitulo())
                .descricao(entity.getDescricao())
                .valor(entity.getValor())
                .limite(entity.getLimite())
                .tipoPlano(entity.getTipoPlano())
                .idPlanoStripe(entity.getIdPlanoStripe())
                .sequencia(entity.getSequencia())
                .servicos(entity.getServicos())
                .build();
    }

    public static PlanoEntity paraEntity(Plano domain) {
        return PlanoEntity.builder()
                .id(domain.getId())
                .titulo(domain.getTitulo())
                .descricao(domain.getDescricao())
                .valor(domain.getValor())
                .limite(domain.getLimite())
                .tipoPlano(domain.getTipoPlano())
                .idPlanoStripe(domain.getIdPlanoStripe())
                .sequencia(domain.getSequencia())
                .servicos(domain.getServicos())
                .build();
    }
}
