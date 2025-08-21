package com.gumeinteligenciacomercial.orcaja.entrypoint.mapper;

import com.gumeinteligenciacomercial.orcaja.domain.CampoPersonalizado;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.CampoPersonalizadoDto;

public class CampoPersonalizadoMapper {

    public static CampoPersonalizadoDto paraDto(CampoPersonalizado domain) {
        return CampoPersonalizadoDto.builder()
                .titulo(domain.getTitulo())
                .valor(domain.getValor())
                .build();
    }

    public static CampoPersonalizado paraDomain(CampoPersonalizadoDto dto) {
        return CampoPersonalizado.builder()
                .titulo(dto.getTitulo())
                .valor(dto.getValor())
                .build();
    }
}
