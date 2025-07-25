package com.gumeinteligenciacomercial.orcaja.entrypoint.mapper;

import com.gumeinteligenciacomercial.orcaja.domain.OrcamentoTradicional;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.OrcamentoTradicionalDto;

public class OrcamentoTradicionalMapper {

    public static OrcamentoTradicionalDto paraDto(OrcamentoTradicional domain) {
        return OrcamentoTradicionalDto.builder()
                .cliente(domain.getCliente())
                .cnpjCpf(domain.getCnpjCpf())
                .produtos(domain.getProdutos().stream().map(ProdutoOrcamentoMapper::paraDto).toList())
                .observacoes(domain.getObservacoes())
                .camposPersonalizados(domain.getCamposPersonalizados().stream().map(CampoPersonalizadoMapper::paraDto).toList())
                .build();
    }

    public static OrcamentoTradicional paraDomain(OrcamentoTradicionalDto dto) {
        return OrcamentoTradicional.builder()
                .cliente(dto.getCliente())
                .cnpjCpf(dto.getCnpjCpf())
                .produtos(dto.getProdutos().stream().map(ProdutoOrcamentoMapper::paraDomain).toList())
                .observacoes(dto.getObservacoes())
                .camposPersonalizados(dto.getCamposPersonalizados().stream().map(CampoPersonalizadoMapper::paraDomain).toList())
                .build();
    }
}
