package com.gumeinteligenciacomercial.orcaja.application.gateway;

import com.gumeinteligenciacomercial.orcaja.domain.Prompt;

import java.util.List;
import java.util.Optional;

public interface PromptGateway {
    Optional<Prompt> buscarPorIdAtivo(String idPrompt);
}
