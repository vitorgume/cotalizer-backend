package com.gumeinteligenciacomercial.orcaja.infrastructure.repositories.entities;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Document(collection = "templates")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class TemplateEntity {

    @MongoId
    private String id;
    private String nomeArquivo;
}
