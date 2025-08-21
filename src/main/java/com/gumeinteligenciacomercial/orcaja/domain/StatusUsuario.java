package com.gumeinteligenciacomercial.orcaja.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum StatusUsuario {
    ATIVO("Ativo", 0),
    PENDENTE_VALIDACAO_EMAIL("Pendente a confirmação de email", 1),
    INATIVO("Inativo", 2);

    private final String descricao;
    private final int codigo;
}
