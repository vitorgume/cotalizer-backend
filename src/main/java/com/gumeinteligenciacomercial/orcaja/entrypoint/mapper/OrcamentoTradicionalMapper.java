package com.gumeinteligenciacomercial.orcaja.entrypoint.mapper;

import com.gumeinteligenciacomercial.orcaja.domain.OrcamentoTradicional;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.OrcamentoTradicionalDto;
import org.springframework.data.domain.Page;

public class OrcamentoTradicionalMapper {

    public static OrcamentoTradicionalDto paraDto(OrcamentoTradicional domain) {
        return OrcamentoTradicionalDto.builder()
                .id(domain.getId())
                .cliente(domain.getCliente())
                .cnpjCpf(domain.getCnpjCpf())
                .produtos(domain.getProdutos().stream().map(ProdutoOrcamentoMapper::paraDto).toList())
                .observacoes(domain.getObservacoes())
                .camposPersonalizados(domain.getCamposPersonalizados().stream().map(CampoPersonalizadoMapper::paraDto).toList())
                .urlArquivo(domain.getUrlArquivo())
                .idUsuario(domain.getIdUsuario())
                .valorTotal(domain.getValorTotal())
                .tipoOrcamento(domain.getTipoOrcamento())
                .status(domain.getStatus())
                .dataCriacao(domain.getDataCriacao())
                .build();
    }

    public static OrcamentoTradicional paraDomain(OrcamentoTradicionalDto dto) {
        return OrcamentoTradicional.builder()
                .id(dto.getId())
                .cliente(dto.getCliente())
                .cnpjCpf(dto.getCnpjCpf())
                .produtos(dto.getProdutos().stream().map(ProdutoOrcamentoMapper::paraDomain).toList())
                .observacoes(dto.getObservacoes())
                .camposPersonalizados(dto.getCamposPersonalizados().stream().map(CampoPersonalizadoMapper::paraDomain).toList())
                .urlArquivo(dto.getUrlArquivo())
                .idUsuario(dto.getIdUsuario())
                .valorTotal(dto.getValorTotal())
                .tipoOrcamento(dto.getTipoOrcamento())
                .status(dto.getStatus())
                .dataCriacao(dto.getDataCriacao())
                .build();
    }

    public static Page<OrcamentoTradicionalDto> paraDtos(Page<OrcamentoTradicional> domains) {
        return domains.map(OrcamentoTradicionalMapper::paraDto);
    }
}
