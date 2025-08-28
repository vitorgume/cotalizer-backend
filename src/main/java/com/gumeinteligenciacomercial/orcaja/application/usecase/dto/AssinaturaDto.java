package com.gumeinteligenciacomercial.orcaja.application.usecase.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class AssinaturaDto {
    private String paymentMethodId;
    private String customerEmail;
    private String idUsuario;
}
