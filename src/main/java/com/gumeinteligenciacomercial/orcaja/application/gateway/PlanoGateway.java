package com.gumeinteligenciacomercial.orcaja.application.gateway;

import com.gumeinteligenciacomercial.orcaja.domain.Plano;
import com.gumeinteligenciacomercial.orcaja.domain.TipoPlano;

import java.util.List;
import java.util.Optional;

public interface PlanoGateway {
    List<Plano> listar();

    Optional<Plano> consultarPlanoPeloTipo(TipoPlano tipoPlano);

    Optional<Plano> consultarPlanoInicial();
}
