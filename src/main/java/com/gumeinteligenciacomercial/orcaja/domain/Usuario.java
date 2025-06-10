package com.gumeinteligenciacomercial.orcaja.domain;

import lombok.*;


@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class Usuario {
    private String id;
    private String nome;
    private String email;
    private String telefone;
    private String cpf;
    private String cnpj;
}
