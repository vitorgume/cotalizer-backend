package com.gumeinteligenciacomercial.orcaja.infrastructure.repositories.entities;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Document(collection = "prompts")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PromptEntity {

    @Id
    private String id;
    private String conteudo;
    private String modelIa;
    private Boolean ativo;
}
