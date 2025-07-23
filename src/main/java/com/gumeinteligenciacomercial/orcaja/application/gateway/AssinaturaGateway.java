package com.gumeinteligenciacomercial.orcaja.application.gateway;

import com.gumeinteligenciacomercial.orcaja.application.usecase.dto.AssinaturaDto;

public interface AssinaturaGateway {
    void enviarNovaAssinatura(AssinaturaDto novaAssinatura);

    void enviarCancelamento(String idUsuario);
}
