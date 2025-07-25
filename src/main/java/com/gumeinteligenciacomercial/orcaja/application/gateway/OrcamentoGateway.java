package com.gumeinteligenciacomercial.orcaja.application.gateway;

import com.gumeinteligenciacomercial.orcaja.domain.Orcamento;
import com.gumeinteligenciacomercial.orcaja.domain.OrcamentoTradicional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface OrcamentoGateway {
    Optional<Orcamento> consultarPorId(String idOrcamento);

    Page<Orcamento> listarPorUsuario(String idUsuario, Pageable pageable);

    Orcamento salvar(Orcamento orcamento);

    void deletar(String id);
}
