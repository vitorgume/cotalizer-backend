package com.gumeinteligenciacomercial.orcaja.application.usecase.dto;

import com.gumeinteligenciacomercial.orcaja.domain.Plano;
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
    private Plano plano;
}
