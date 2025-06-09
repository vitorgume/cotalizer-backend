package com.gumeinteligenciacomercial.orcaja.domain;

import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class Usuario {
    private UUID id;
    private String nome;
    private String email;
    private String telefone;
    private String cpf;
    private String cnpj;
}
