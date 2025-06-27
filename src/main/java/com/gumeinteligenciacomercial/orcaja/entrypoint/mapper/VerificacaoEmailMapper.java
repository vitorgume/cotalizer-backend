package com.gumeinteligenciacomercial.orcaja.entrypoint.mapper;

import com.gumeinteligenciacomercial.orcaja.domain.VerificacaoEmail;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.VerificaoEmailDto;

public class VerificacaoEmailMapper {

    public static VerificacaoEmail paraDomain(VerificaoEmailDto dto) {
        return VerificacaoEmail.builder()
                .email(dto.getEmail())
                .codigo(dto.getCodigo())
                .build();
    }

    public static VerificaoEmailDto paraDto(VerificacaoEmail domain) {
        return VerificaoEmailDto.builder()
                .email(domain.getEmail())
                .codigo(domain.getCodigo())
                .build();
    }
}
