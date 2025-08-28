package com.gumeinteligenciacomercial.orcaja.application.gateway;

import com.gumeinteligenciacomercial.orcaja.domain.OrcamentoTradicional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface OrcamentoTradicionalGateway {
    OrcamentoTradicional salvar(OrcamentoTradicional novoOrcamento);

    Optional<OrcamentoTradicional> consultarPorId(String id);

    Page<OrcamentoTradicional> listarPorUsuario(String idUsuario, Pageable pageable);

    void deletar(String id);
}
