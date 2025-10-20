package com.gumeinteligenciacomercial.orcaja.entrypoint.dto;

import com.gumeinteligenciacomercial.orcaja.domain.TipoPlano;
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
    private TipoPlano tipoPlano;
    private Integer sequencia;
    private List<String> servicos;
}
