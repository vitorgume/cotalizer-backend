package com.gumeinteligenciacomercial.orcaja.infrastructure.repositories.entities;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.List;

@Document(collection = "orcamentos_tradicionais")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class OrcamentoTradicionalEntity {

    @MongoId
    private String id;
    private String cliente;
    private String cnpjCpf;
    private List<ProdutoOrcamentoEntity> produtos;
    private String observacoes;
    private List<CampoPersonalizadoEntity> camposPersonalizados;
}
