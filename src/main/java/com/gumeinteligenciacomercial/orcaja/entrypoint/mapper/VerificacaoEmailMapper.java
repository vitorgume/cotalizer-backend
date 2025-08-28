package com.gumeinteligenciacomercial.orcaja.entrypoint.mapper;

import com.gumeinteligenciacomercial.orcaja.domain.VerificacaoEmail;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.VerificacaoEmailDto;

public class VerificacaoEmailMapper {

    public static VerificacaoEmail paraDomain(VerificacaoEmailDto dto) {
        return VerificacaoEmail.builder()
                .email(dto.getEmail())
                .codigo(dto.getCodigo())
                .build();
    }

    public static VerificacaoEmailDto paraDto(VerificacaoEmail domain) {
        return VerificacaoEmailDto.builder()
                .email(domain.getEmail())
                .codigo(domain.getCodigo())
                .build();
    }
}
