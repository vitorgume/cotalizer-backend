package com.gumeinteligenciacomercial.orcaja.infrastructure.repositories.entities;

import com.gumeinteligenciacomercial.orcaja.domain.StatusOrcamento;
import com.gumeinteligenciacomercial.orcaja.domain.TipoOrcamento;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Document(collection = "orcamentos_tradicionais")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class    OrcamentoTradicionalEntity {

    @MongoId
    private String id;
    private String cliente;
    private String cnpjCpf;
    private List<ProdutoOrcamentoEntity> produtos;
    private String observacoes;
    private List<CampoPersonalizadoEntity> camposPersonalizados;
    private String urlArquivo;
    private String idUsuario;
    private BigDecimal valorTotal;
    private TipoOrcamento tipoOrcamento;
    private StatusOrcamento status;
    private LocalDate dataCriacao;
}
