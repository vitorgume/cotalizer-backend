package com.gumeinteligenciacomercial.orcaja.infrastructure.dataprovider;

import com.gumeinteligenciacomercial.orcaja.application.gateway.PromptGateway;
import com.gumeinteligenciacomercial.orcaja.domain.Prompt;
import com.gumeinteligenciacomercial.orcaja.infrastructure.exceptions.DataProviderException;
import com.gumeinteligenciacomercial.orcaja.infrastructure.mapper.PromptMapper;
import com.gumeinteligenciacomercial.orcaja.infrastructure.repositories.PromptRepository;
import com.gumeinteligenciacomercial.orcaja.infrastructure.repositories.entities.PromptEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PromptDataProvider implements PromptGateway {

    private final PromptRepository repository;
    private final String MENSAMGE_ERRO_BUSCAR_PROMPT_ATIVO = "Erro ao buscar por prompts ativos.";

    @Override
    public List<Prompt> buscarAtivo() {
        List<PromptEntity> promptEntities;

        try {
            promptEntities = repository.findByAtivoTrue();
        } catch (Exception ex) {
            log.error(MENSAMGE_ERRO_BUSCAR_PROMPT_ATIVO, ex);
            throw new DataProviderException(MENSAMGE_ERRO_BUSCAR_PROMPT_ATIVO, ex.getCause());
        }

        return promptEntities.stream().map(PromptMapper::paraDomain).toList();
    }
}
