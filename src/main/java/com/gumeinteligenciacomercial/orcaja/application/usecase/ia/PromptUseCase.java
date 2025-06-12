package com.gumeinteligenciacomercial.orcaja.application.usecase.ia;

import com.gumeinteligenciacomercial.orcaja.application.gateway.PromptGateway;
import com.gumeinteligenciacomercial.orcaja.domain.Prompt;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PromptUseCase {

    private final PromptGateway gateway;

    public Prompt buscarAtivo() {
        log.info("Buscando prompt ativo");

        List<Prompt> prompts = gateway.buscarAtivo();
        Prompt prompt = prompts.getFirst();

        log.info("Prompt buscado com sucesso.");

        return prompt;
    }
}
