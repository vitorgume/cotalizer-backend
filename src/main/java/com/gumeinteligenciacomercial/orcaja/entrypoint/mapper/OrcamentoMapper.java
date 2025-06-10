package com.gumeinteligenciacomercial.orcaja.entrypoint.mapper;

import com.gumeinteligenciacomercial.orcaja.domain.Orcamento;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.OrcamentoDto;
import org.springframework.data.domain.Page;

public class OrcamentoMapper {

    public static OrcamentoDto paraDto(Orcamento domain) {
        return OrcamentoDto.builder()
                .id(domain.getId())
                .dataCriacao(domain.getDataCriacao())
                .titulo(domain.getTitulo())
                .conteudoOriginal(domain.getConteudoOriginal())
                .usuarioId(domain.getUsuarioId())
                .urlArquivo(domain.getUrlArquivo())
                .build();
    }

    public static Orcamento paraDomain(OrcamentoDto dto) {
        return Orcamento.builder()
                .id(dto.getId())
                .dataCriacao(dto.getDataCriacao())
                .titulo(dto.getTitulo())
                .conteudoOriginal(dto.getConteudoOriginal())
                .usuarioId(dto.getUsuarioId())
                .urlArquivo(dto.getUrlArquivo())
                .build();
    }

    public static Page<OrcamentoDto> paraDtos(Page<Orcamento> domains) {
        return domains.map(OrcamentoMapper::paraDto);
    }
}
