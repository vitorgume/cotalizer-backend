package com.gumeinteligenciacomercial.orcaja.entrypoint.mapper;

import com.gumeinteligenciacomercial.orcaja.domain.Plano;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.PlanoDto;

public class PlanoMapper {

    public static PlanoDto paraDto(Plano domain) {
        return PlanoDto.builder()
                .id(domain.getId())
                .titulo(domain.getTitulo())
                .descricao(domain.getDescricao())
                .valor(domain.getValor())
                .limite(domain.getLimite())
                .padrao(domain.getPadrao())
                .sequencia(domain.getSequencia())
                .servicos(domain.getServicos())
                .build();
    }

    public static Plano paraDomain(PlanoDto dto) {
        return Plano.builder()
                .id(dto.getId())
                .titulo(dto.getTitulo())
                .descricao(dto.getDescricao())
                .valor(dto.getValor())
                .limite(dto.getLimite())
                .padrao(dto.getPadrao())
                .sequencia(dto.getSequencia())
                .servicos(dto.getServicos())
                .build();
    }
}
