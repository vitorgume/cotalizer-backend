package com.gumeinteligenciacomercial.orcaja.application.usecase;

import com.gumeinteligenciacomercial.orcaja.application.gateway.PlanoGateway;
import com.gumeinteligenciacomercial.orcaja.domain.Plano;
import io.netty.handler.codec.http2.Http2Connection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlanoUseCase {

    private final PlanoGateway gateway;

    public List<Plano> listar() {
        return gateway.listar();
    }
}
