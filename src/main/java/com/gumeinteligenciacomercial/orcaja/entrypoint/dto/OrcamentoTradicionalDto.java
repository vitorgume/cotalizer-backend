package com.gumeinteligenciacomercial.orcaja.entrypoint.dto;

import com.gumeinteligenciacomercial.orcaja.domain.CampoPersonalizado;
import com.gumeinteligenciacomercial.orcaja.domain.ProdutoOrcamento;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class OrcamentoTradicionalDto {
    private String id;
    private String cliente;
    private String cnpjCpf;
    private List<ProdutoOrcamentoDto> produtos;
    private String observacoes;
    private List<CampoPersonalizadoDto> camposPersonalizados;
}
