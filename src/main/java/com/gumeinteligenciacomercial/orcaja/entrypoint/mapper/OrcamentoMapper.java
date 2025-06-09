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
                .conteudo(domain.getConteudo())
                .usuarioDto(UsuarioMapper.paraDto(domain.getUsuario()))
                .build();
    }

    public static Orcamento paraDomain(OrcamentoDto dto) {
        return Orcamento.builder()
                .id(dto.getId())
                .dataCriacao(dto.getDataCriacao())
                .titulo(dto.getTitulo())
                .conteudo(dto.getConteudo())
                .usuario(UsuarioMapper.paraDomain(dto.getUsuarioDto()))
                .build();
    }

    public static Page<OrcamentoDto> paraDtos(Page<Orcamento> domains) {
        return domains.map(OrcamentoMapper::paraDto);
    }
}
