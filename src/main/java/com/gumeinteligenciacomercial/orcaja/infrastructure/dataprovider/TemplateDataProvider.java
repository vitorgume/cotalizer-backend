package com.gumeinteligenciacomercial.orcaja.infrastructure.dataprovider;

import com.gumeinteligenciacomercial.orcaja.application.gateway.TemplateGateway;
import com.gumeinteligenciacomercial.orcaja.domain.Template;
import com.gumeinteligenciacomercial.orcaja.infrastructure.exceptions.DataProviderException;
import com.gumeinteligenciacomercial.orcaja.infrastructure.mapper.TemplateMapper;
import com.gumeinteligenciacomercial.orcaja.infrastructure.repositories.TemplateRepository;
import com.gumeinteligenciacomercial.orcaja.infrastructure.repositories.entities.TemplateEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class TemplateDataProvider implements TemplateGateway {

    private final TemplateRepository repository;
    private static final String MENSAGEM_ERRO_LISTAR_TODOS = "Erro ao listar todos os templates.";

    @Override
    public List<Template> listarTodos() {
        List<TemplateEntity> templateEntityList;

        try {
            templateEntityList = repository.findAll();
        } catch (Exception ex) {
            log.error(MENSAGEM_ERRO_LISTAR_TODOS, ex);
            throw new DataProviderException(MENSAGEM_ERRO_LISTAR_TODOS, ex.getCause());
        }

        return templateEntityList.stream().map(TemplateMapper::paraDomain).toList();
    }
}
