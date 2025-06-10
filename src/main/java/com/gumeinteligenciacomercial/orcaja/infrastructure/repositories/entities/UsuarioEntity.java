package com.gumeinteligenciacomercial.orcaja.infrastructure.repositories.entities;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "orcamentos")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UsuarioEntity {

    @Id
    private String id;
    private String nome;
    private String email;
    private String telefone;
    private String cpf;
    private String cnpj;
}
