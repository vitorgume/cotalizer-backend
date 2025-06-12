package com.gumeinteligenciacomercial.orcaja.infrastructure.repositories.entities;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Document(collection = "prompts")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PromptEntity {

    @MongoId
    private String id;
    private String conteudo;
    private String modelIa;
    private Boolean ativo;
}
