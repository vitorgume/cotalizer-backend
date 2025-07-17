package com.gumeinteligenciacomercial.orcaja.domain;

import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class Assinatura {
    private String id;
    private String cardTokenId;
    private String email;
    private String paymentMethodId;
    private String status;
    private String idUsuario;
}
