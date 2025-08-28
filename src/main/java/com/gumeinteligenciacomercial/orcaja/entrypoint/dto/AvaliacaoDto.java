package com.gumeinteligenciacomercial.orcaja.entrypoint.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class AvaliacaoDto {
    private String idUsuario;
    private Integer nota;
    private String motivoNota;
    private String sugestaoMelhoria;
}
