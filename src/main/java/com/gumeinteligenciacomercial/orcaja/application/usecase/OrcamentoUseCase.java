package com.gumeinteligenciacomercial.orcaja.application.usecase;

import com.gumeinteligenciacomercial.orcaja.domain.Orcamento;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrcamentoUseCase {

    private final OrcamentoGateway gateway;

    public Orcamento cadastrar(Orcamento orcamento) {
        


        return null;
    }

    public Orcamento consultarPorId(UUID idOrcamento) {
    }

    public Page<Orcamento> listarPorUsuario(UUID idUsuario, Pageable pageable) {

    }
}
