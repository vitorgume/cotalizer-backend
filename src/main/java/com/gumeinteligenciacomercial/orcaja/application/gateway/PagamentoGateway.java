package com.gumeinteligenciacomercial.orcaja.application.gateway;

import com.gumeinteligenciacomercial.orcaja.domain.Assinatura;

public interface PagamentoGateway {

    Assinatura criarAssinatura(Assinatura novaAssinatura);
}
