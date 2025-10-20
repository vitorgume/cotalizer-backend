package com.gumeinteligenciacomercial.orcaja.application.usecase;

import com.gumeinteligenciacomercial.orcaja.application.exceptions.PlanoNaoEncontradoException;
import com.gumeinteligenciacomercial.orcaja.application.gateway.PlanoGateway;
import com.gumeinteligenciacomercial.orcaja.domain.Plano;
import com.gumeinteligenciacomercial.orcaja.domain.TipoPlano;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PlanoUseCase {

    private final PlanoGateway gateway;

    public List<Plano> listar() {
        List<Plano> planos =  gateway.listar();
        return planos;
    }

    public Plano consultarPlanoPeloTipo(TipoPlano tipoPlano) {
        Optional<Plano> plano = gateway.consultarPlanoPeloTipo(tipoPlano);

        if(plano.isEmpty()) {
            throw new PlanoNaoEncontradoException();
        }

        return plano.get();
    }

    public Plano consultarPlanoInicial() {
        Optional<Plano> plano = gateway.consultarPlanoInicial();

        if(plano.isEmpty()) {
            throw new PlanoNaoEncontradoException();
        }

        return plano.get();
    }
}
