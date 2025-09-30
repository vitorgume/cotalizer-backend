package com.gumeinteligenciacomercial.orcaja.infrastructure.mapper;

import com.gumeinteligenciacomercial.orcaja.domain.OrcamentoTradicional;
import com.gumeinteligenciacomercial.orcaja.infrastructure.repositories.entities.OrcamentoTradicionalEntity;

public class OrcamentoTradicionalMapper {

    public static OrcamentoTradicional paraDomain(OrcamentoTradicionalEntity entity) {
        return OrcamentoTradicional.builder()
                .id(entity.getId())
                .cliente(entity.getCliente())
                .cnpjCpf(entity.getCnpjCpf())
                .produtos(entity.getProdutos().stream().map(ProdutoOrcamentoMapper::paraDomain).toList())
                .observacoes(entity.getObservacoes())
                .camposPersonalizados(entity.getCamposPersonalizados().stream().map(CampoPersonalizadoMapper::paraDomain).toList())
                .urlArquivo(entity.getUrlArquivo())
                .idUsuario(entity.getIdUsuario())
                .valorTotal(entity.getValorTotal())
                .tipoOrcamento(entity.getTipoOrcamento())
                .status(entity.getStatus())
                .dataCriacao(entity.getDataCriacao())
                .template(TemplateMapper.paraDomain(entity.getTemplate()))
                .build();
    }

    public static OrcamentoTradicionalEntity paraEntity(OrcamentoTradicional domain) {
        return OrcamentoTradicionalEntity.builder()
                .id(domain.getId())
                .cliente(domain.getCliente())
                .cnpjCpf(domain.getCnpjCpf())
                .produtos(domain.getProdutos().stream().map(ProdutoOrcamentoMapper::paraEntity).toList())
                .observacoes(domain.getObservacoes())
                .camposPersonalizados(domain.getCamposPersonalizados().stream().map(CampoPersonalizadoMapper::paraEntity).toList())
                .urlArquivo(domain.getUrlArquivo())
                .idUsuario(domain.getIdUsuario())
                .valorTotal(domain.getValorTotal())
                .tipoOrcamento(domain.getTipoOrcamento())
                .status(domain.getStatus())
                .dataCriacao(domain.getDataCriacao())
                .template(TemplateMapper.paraEntity(domain.getTemplate()))
                .build();
    }
}
