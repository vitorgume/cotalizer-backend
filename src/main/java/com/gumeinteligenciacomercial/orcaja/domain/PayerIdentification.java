package com.gumeinteligenciacomercial.orcaja.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class PayerIdentification {
    private String type;
    private String number;
}
