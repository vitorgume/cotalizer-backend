package com.gumeinteligenciacomercial.orcaja.entrypoint.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class AssinaturaRequestDto {
    private String cardTokenId;
    private String email;
    private String paymentMethodId;
    private String idUsuario;
}
