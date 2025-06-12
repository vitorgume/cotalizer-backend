package com.gumeinteligenciacomercial.orcaja.infrastructure.repositories.entities;

import jakarta.persistence.Id;
import lombok.*;
import lombok.extern.java.Log;
import org.springframework.data.mongodb.core.mapping.Document;

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
