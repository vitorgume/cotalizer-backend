package com.gumeinteligenciacomercial.orcaja.application.usecase.ia;

import com.gumeinteligenciacomercial.orcaja.application.exceptions.PromptNaoEncontradoException;
import com.gumeinteligenciacomercial.orcaja.application.gateway.PromptGateway;
import com.gumeinteligenciacomercial.orcaja.domain.Prompt;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PromptUseCase {

    private final PromptGateway gateway;

    public Prompt buscarPorIdAtivo(String idPrompt) {
        log.info("Buscando prompt ativo");

        Optional<Prompt> prompt = gateway.buscarPorIdAtivo(idPrompt);

        if(prompt.isEmpty()) {
            throw new PromptNaoEncontradoException();
        }

        log.info("Prompt buscado com sucesso.");

        return prompt.get();
    }
}
