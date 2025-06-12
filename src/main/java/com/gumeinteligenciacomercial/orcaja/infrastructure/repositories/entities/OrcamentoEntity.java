package com.gumeinteligenciacomercial.orcaja.infrastructure.repositories.entities;

import lombok.*;
import org.bson.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDate;

@org.springframework.data.mongodb.core.mapping.Document(collection = "orcamentos")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class OrcamentoEntity {

    @MongoId
    private String id;
    private String conteudoOriginal;
    private Document orcamentoFormatado;
    private String urlArquivo;
    private LocalDate dataCriacao;
    private String titulo;
    private String idUsuario;
}
