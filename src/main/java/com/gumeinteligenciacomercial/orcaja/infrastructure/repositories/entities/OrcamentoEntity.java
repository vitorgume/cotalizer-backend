package com.gumeinteligenciacomercial.orcaja.infrastructure.repositories.entities;

import com.gumeinteligenciacomercial.orcaja.domain.StatusOrcamento;
import com.gumeinteligenciacomercial.orcaja.domain.TipoOrcamento;
import lombok.*;
import org.bson.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDate;

@org.springframework.data.mongodb.core.mapping.Document(collection = "orcamentos_ia")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class OrcamentoEntity {

    @MongoId
    private String id;
    private String conteudoOriginal;
    private Document orcamentoFormatado;
    private String urlArquivo;
    private LocalDate dataCriacao;
    private String titulo;
    private String idUsuario;
    private StatusOrcamento status;
    private TipoOrcamento tipoOrcamento;
}
