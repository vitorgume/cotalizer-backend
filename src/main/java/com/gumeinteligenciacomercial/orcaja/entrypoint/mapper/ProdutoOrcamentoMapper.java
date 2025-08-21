package com.gumeinteligenciacomercial.orcaja.entrypoint.mapper;

import com.gumeinteligenciacomercial.orcaja.domain.ProdutoOrcamento;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.ProdutoOrcamentoDto;

public class ProdutoOrcamentoMapper {

    public static ProdutoOrcamentoDto paraDto(ProdutoOrcamento domain) {
        return ProdutoOrcamentoDto.builder()
                .descricao(domain.getDescricao())
                .valor(domain.getValor())
                .quantidade(domain.getQuantidade())
                .build();
    }

    public static ProdutoOrcamento paraDomain(ProdutoOrcamentoDto dto) {
        return ProdutoOrcamento.builder()
                .descricao(dto.getDescricao())
                .valor(dto.getValor())
                .quantidade(dto.getQuantidade())
                .build();
    }
}
