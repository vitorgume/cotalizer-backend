package com.gumeinteligenciacomercial.orcaja.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum StatusOrcamento {
    APROVADO(0, "Aprovado"),
    REPROVADO(1, "Reprovado");

    private final int codigo;
    private final String descricao;
}
