package com.gumeinteligenciacomercial.orcaja.infrastructure.mapper;

import com.gumeinteligenciacomercial.orcaja.domain.Orcamento;
import com.gumeinteligenciacomercial.orcaja.infrastructure.repositories.entities.OrcamentoEntity;
import org.bson.Document;

import java.util.HashMap;
import java.util.Map;

public class OrcamentoMapper {
    public static OrcamentoEntity paraEntity(Orcamento domain) {
        return OrcamentoEntity.builder()
                .id(domain.getId())
                .conteudoOriginal(domain.getConteudoOriginal())
                .orcamentoFormatado(new Document(domain.getOrcamentoFormatado()))
                .dataCriacao(domain.getDataCriacao())
                .titulo(domain.getTitulo())
                .idUsuario(domain.getUsuarioId())
                .urlArquivo(domain.getUrlArquivo())
                .status(domain.getStatus())
                .build();
    }

    public static Orcamento paraDomain(OrcamentoEntity entity) {
        return Orcamento.builder()
                .id(entity.getId())
                .conteudoOriginal(entity.getConteudoOriginal())
                .orcamentoFormatado(documentToMap(entity.getOrcamentoFormatado()))
                .dataCriacao(entity.getDataCriacao())
                .titulo(entity.getTitulo())
                .usuarioId(entity.getIdUsuario())
                .urlArquivo(entity.getUrlArquivo())
                .status(entity.getStatus())
                .build();
    }

    private static Map<String, Object> documentToMap(Document document) {
        return document == null ? new HashMap<>() : new HashMap<>(document);
    }
}
