package com.gumeinteligenciacomercial.orcaja.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class VerificacaoEmail {
    private String email;
    private String codigo;
}
