package com.gumeinteligenciacomercial.orcaja.application.gateway;

import com.gumeinteligenciacomercial.orcaja.domain.Prompt;

import java.util.List;

public interface PromptGateway {
    List<Prompt> buscarAtivo();
}
