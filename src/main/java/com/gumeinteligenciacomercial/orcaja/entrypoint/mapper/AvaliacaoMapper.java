package com.gumeinteligenciacomercial.orcaja.entrypoint.mapper;

import com.gumeinteligenciacomercial.orcaja.domain.Avaliacao;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.AvaliacaoDto;

public class AvaliacaoMapper {
    public static Avaliacao paraDomain(AvaliacaoDto dto) {
        return Avaliacao.builder()
                .idUsuario(dto.getIdUsuario())
                .nota(dto.getNota())
                .sugestaoMelhoria(dto.getSugestaoMelhoria())
                .motivoNota(dto.getMotivoNota())
                .build();
    }

    public static AvaliacaoDto paraDto(Avaliacao domain) {
        return AvaliacaoDto.builder()
                .idUsuario(domain.getIdUsuario())
                .nota(domain.getNota())
                .sugestaoMelhoria(domain.getSugestaoMelhoria())
                .motivoNota(domain.getMotivoNota())
                .build();
    }
}
