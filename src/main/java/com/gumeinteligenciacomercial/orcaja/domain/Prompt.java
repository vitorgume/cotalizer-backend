package com.gumeinteligenciacomercial.orcaja.domain;

import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class Prompt {
    private String id;
    private String modelIa;
    private String conteudo;
    private Boolean ativo;
}
