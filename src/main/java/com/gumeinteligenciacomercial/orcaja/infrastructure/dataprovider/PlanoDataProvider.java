package com.gumeinteligenciacomercial.orcaja.infrastructure.dataprovider;

import com.gumeinteligenciacomercial.orcaja.application.gateway.PlanoGateway;
import com.gumeinteligenciacomercial.orcaja.domain.Plano;
import com.gumeinteligenciacomercial.orcaja.infrastructure.exceptions.DataProviderException;
import com.gumeinteligenciacomercial.orcaja.infrastructure.mapper.PlanoMapper;
import com.gumeinteligenciacomercial.orcaja.infrastructure.repositories.PlanoRepository;
import com.gumeinteligenciacomercial.orcaja.infrastructure.repositories.entities.PlanoEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class PlanoDataProvider implements PlanoGateway {

    private final PlanoRepository repository;
    private static final String MENSAGEM_ERRO_LISTAR = "Erro ao listar planos.";
    private static final String MENSAGEM_ERRO_CONSULTAR_PADRAO = "Erro ao consultar plano padrão.";

    @Override
    public List<Plano> listar() {
        List<PlanoEntity> planoEntityList;

        try {
            planoEntityList = repository.findAll();
        } catch (Exception ex) {
            log.error(MENSAGEM_ERRO_LISTAR, ex);
            throw new DataProviderException(MENSAGEM_ERRO_LISTAR, ex.getCause());
        }

        return planoEntityList.stream().map(PlanoMapper::paraDomain).toList();
    }

    @Override
    public Optional<Plano> consultarPlanoPadrao() {
        Optional<PlanoEntity> planoEntity;

        try {
            planoEntity = repository.findByPadraoTrue();
        } catch (Exception ex) {
            log.error(MENSAGEM_ERRO_CONSULTAR_PADRAO, ex);
            throw new DataProviderException(MENSAGEM_ERRO_CONSULTAR_PADRAO, ex.getCause());
        }

        return planoEntity.map(PlanoMapper::paraDomain);
    }
}
