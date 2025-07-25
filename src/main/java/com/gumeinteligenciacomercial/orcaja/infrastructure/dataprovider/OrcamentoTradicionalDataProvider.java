package com.gumeinteligenciacomercial.orcaja.infrastructure.dataprovider;

import com.gumeinteligenciacomercial.orcaja.application.gateway.OrcamentoTradicionalGateway;
import com.gumeinteligenciacomercial.orcaja.domain.OrcamentoTradicional;
import com.gumeinteligenciacomercial.orcaja.infrastructure.exceptions.DataProviderException;
import com.gumeinteligenciacomercial.orcaja.infrastructure.mapper.OrcamentoTradicionalMapper;
import com.gumeinteligenciacomercial.orcaja.infrastructure.repositories.OrcamentoTradicionalRepository;
import com.gumeinteligenciacomercial.orcaja.infrastructure.repositories.entities.OrcamentoTradicionalEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrcamentoTradicionalDataProvider implements OrcamentoTradicionalGateway {

    private final OrcamentoTradicionalRepository repository;
    private final String MENSAGEM_ERRO_CADASTRAR_ORCAMENTO_TRADICIONAL = "Erro ao salvar orçamento tradicional.";

    @Override
    public OrcamentoTradicional salvar(OrcamentoTradicional novoOrcamento) {
        OrcamentoTradicionalEntity orcamentoTradicionalEntity = OrcamentoTradicionalMapper.paraEntity(novoOrcamento);

        try {
            orcamentoTradicionalEntity = repository.save(orcamentoTradicionalEntity);
        } catch (Exception ex) {
            log.error(MENSAGEM_ERRO_CADASTRAR_ORCAMENTO_TRADICIONAL, ex);
            throw new DataProviderException(MENSAGEM_ERRO_CADASTRAR_ORCAMENTO_TRADICIONAL, ex.getCause());
        }

        return OrcamentoTradicionalMapper.paraDomain(orcamentoTradicionalEntity);
    }
}
