package com.gumeinteligenciacomercial.orcaja.domain;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class OrcamentoTradicional {
    private String id;
    private String cliente;
    private String cnpjCpf;
    private List<ProdutoOrcamento> produtos;
    private String observacoes;
    private List<CampoPersonalizado> camposPersonalizados;
}
