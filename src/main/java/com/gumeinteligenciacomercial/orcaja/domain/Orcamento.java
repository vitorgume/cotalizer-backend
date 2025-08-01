package com.gumeinteligenciacomercial.orcaja.domain;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class Orcamento {
    private String id;
    private String conteudoOriginal;
    private Map<String, Object> orcamentoFormatado;
    private String urlArquivo;
    private LocalDate dataCriacao;
    private String titulo;
    private String usuarioId;
    private StatusOrcamento status;
    private TipoOrcamento tipoOrcamento;
    private BigDecimal valorTotal;

    public void setDados(Orcamento novoOrcamento) {
        this.orcamentoFormatado = novoOrcamento.getOrcamentoFormatado();
        this.titulo = novoOrcamento.getTitulo();
        this.urlArquivo = novoOrcamento.getUrlArquivo();
        this.status = novoOrcamento.getStatus();
    }
}
