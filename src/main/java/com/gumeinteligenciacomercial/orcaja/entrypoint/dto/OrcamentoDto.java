package com.gumeinteligenciacomercial.orcaja.entrypoint.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Map;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class OrcamentoDto {
    private String id;
    private String conteudoOriginal;
    private LocalDate dataCriacao;
    private Map<String, Object> orcamentoFormatado;
    private String titulo;
    private String urlArquivo;
    private String usuarioId;
}
