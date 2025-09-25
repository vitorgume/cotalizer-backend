package com.gumeinteligenciacomercial.orcaja.entrypoint.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class PlanoDto {
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
