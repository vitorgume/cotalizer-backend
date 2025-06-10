package com.gumeinteligenciacomercial.orcaja.infrastructure.repositories.entities;

import jakarta.persistence.Id;
import lombok.*;
import org.bson.Document;

import java.time.LocalDate;

@org.springframework.data.mongodb.core.mapping.Document(collection = "orcamentos")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class OrcamentoEntity {

    @Id
    private String id;
    private String conteudoOriginal;
    private Document orcamentoFormatado;
    private String urlArquivo;
    private LocalDate dataCriacao;
    private String titulo;
    private String idUsuario;
}
