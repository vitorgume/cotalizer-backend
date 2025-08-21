package com.gumeinteligenciacomercial.orcaja.domain;

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
public class OrcamentoTradicional {
    private String id;
    private String cliente;
    private String cnpjCpf;
    private List<ProdutoOrcamento> produtos;
    private String observacoes;
    private List<CampoPersonalizado> camposPersonalizados;
    private String urlArquivo;
    private String idUsuario;
    private BigDecimal valorTotal;
    private TipoOrcamento tipoOrcamento;
    private StatusOrcamento status;
    private LocalDate dataCriacao;

    public void setDados(OrcamentoTradicional orcamentoTradicional) {
        this.cliente = orcamentoTradicional.getCliente();
        this.cnpjCpf = orcamentoTradicional.getCnpjCpf();
        this.produtos = orcamentoTradicional.getProdutos();
        this.observacoes = orcamentoTradicional.getObservacoes();
        this.camposPersonalizados = orcamentoTradicional.getCamposPersonalizados();
        this.urlArquivo = orcamentoTradicional.getUrlArquivo();
        this.idUsuario = orcamentoTradicional.getIdUsuario();
        this.valorTotal = orcamentoTradicional.getValorTotal();
        this.status = orcamentoTradicional.getStatus();
    }
}
