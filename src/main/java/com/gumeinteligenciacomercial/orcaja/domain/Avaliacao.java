package com.gumeinteligenciacomercial.orcaja.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class Avaliacao {
    private String idUsuario;
    private Integer nota;
    private String motivoNota;
    private String sugestaoMelhoria;
}
