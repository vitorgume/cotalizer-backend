package com.gumeinteligenciacomercial.orcaja.entrypoint.mapper;

import com.gumeinteligenciacomercial.orcaja.domain.Template;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.TemplateDto;

public class TemplateMapper {

    public static Template paraDomain(TemplateDto dto) {
        return Template.builder()
                .id(dto.getId())
                .nomeArquivo(dto.getNomeArquivo())
                .build();
    }

    public static TemplateDto paraDto(Template domain) {
        return TemplateDto.builder()
                .id(domain.getId())
                .nomeArquivo(domain.getNomeArquivo())
                .build();
    }
}
