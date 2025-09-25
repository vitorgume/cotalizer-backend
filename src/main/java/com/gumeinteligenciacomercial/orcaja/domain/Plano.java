package com.gumeinteligenciacomercial.orcaja.domain;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class Plano {
    private String id;
    private String titulo;
    private String descricao;
    private BigDecimal valor;
    private Integer limite;
    private String idPlanoStripe;
    private Boolean padrao;
    private Integer sequencia;
    private List<String> servicos;
}
