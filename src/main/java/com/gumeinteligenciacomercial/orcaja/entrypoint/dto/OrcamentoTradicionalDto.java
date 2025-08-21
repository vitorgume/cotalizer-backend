package com.gumeinteligenciacomercial.orcaja.entrypoint.dto;

import com.gumeinteligenciacomercial.orcaja.domain.StatusOrcamento;
import com.gumeinteligenciacomercial.orcaja.domain.TipoOrcamento;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private String urlArquivo;
    private String idUsuario;
    private BigDecimal valorTotal;
    private TipoOrcamento tipoOrcamento;
    private StatusOrcamento status;
    private LocalDate dataCriacao;
}
