package com.gumeinteligenciacomercial.orcaja.infrastructure.repositories.entities;

import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class ProdutoOrcamentoEntity {
    private String descricao;
    private BigDecimal valor;
    private Integer quantidade;
}
