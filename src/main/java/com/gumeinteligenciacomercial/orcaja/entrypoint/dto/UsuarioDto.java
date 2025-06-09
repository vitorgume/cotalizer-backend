package com.gumeinteligenciacomercial.orcaja.entrypoint.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class UsuarioDto {
    private UUID id;
    private String nome;
    private String email;
    private String telefone;
    private String cpf;
    private String cnpj;
}
