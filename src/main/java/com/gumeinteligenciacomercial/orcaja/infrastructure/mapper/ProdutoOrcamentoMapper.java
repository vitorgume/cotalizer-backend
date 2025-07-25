package com.gumeinteligenciacomercial.orcaja.infrastructure.mapper;

import com.gumeinteligenciacomercial.orcaja.domain.ProdutoOrcamento;
import com.gumeinteligenciacomercial.orcaja.infrastructure.repositories.entities.ProdutoOrcamentoEntity;

public class ProdutoOrcamentoMapper {

    public static ProdutoOrcamento paraDomain(ProdutoOrcamentoEntity entity) {
        return ProdutoOrcamento.builder()
                .descricao(entity.getDescricao())
                .valor(entity.getValor())
                .quantidade(entity.getQuantidade())
                .build();
    }

    public static ProdutoOrcamentoEntity paraEntity(ProdutoOrcamento domain) {
        return ProdutoOrcamentoEntity.builder()
                .descricao(domain.getDescricao())
                .valor(domain.getValor())
                .quantidade(domain.getQuantidade())
                .build();
    }
}
