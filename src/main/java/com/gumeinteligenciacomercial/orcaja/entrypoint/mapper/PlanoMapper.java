package com.gumeinteligenciacomercial.orcaja.entrypoint.mapper;

import com.gumeinteligenciacomercial.orcaja.domain.Plano;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.PlanoDto;

public class PlanoMapper {

    public static PlanoDto paraDto(Plano domain) {
        return PlanoDto.builder()
                .id(domain.getId())
                .titulo(domain.getTitulo())
                .valor(domain.getValor())
                .limite(domain.getLimite())
                .padrao(domain.getPadrao())
                .grau(domain.getGrau())
                .build();
    }
}
