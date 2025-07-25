package com.gumeinteligenciacomercial.orcaja.domain;

import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class ProdutoOrcamento {
    private String descricao;
    private BigDecimal valor;
    private Integer quantidade;
}
