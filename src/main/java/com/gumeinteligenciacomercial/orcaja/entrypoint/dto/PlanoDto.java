package com.gumeinteligenciacomercial.orcaja.entrypoint.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class PlanoDto {
    private String id;
    private String titulo;
    private BigDecimal valor;
    private Integer limite;
    private String idPlanoStripe;
    private Boolean padrao;
    private Integer grau;
}
