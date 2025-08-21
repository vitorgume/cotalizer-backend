package com.gumeinteligenciacomercial.orcaja.infrastructure.dataprovider;

import com.gumeinteligenciacomercial.orcaja.application.gateway.OrcamentoGateway;
import com.gumeinteligenciacomercial.orcaja.domain.Orcamento;
import com.gumeinteligenciacomercial.orcaja.infrastructure.exceptions.DataProviderException;
import com.gumeinteligenciacomercial.orcaja.infrastructure.mapper.OrcamentoMapper;
import com.gumeinteligenciacomercial.orcaja.infrastructure.repositories.OrcamentoRepository;
import com.gumeinteligenciacomercial.orcaja.infrastructure.repositories.OrcamentoTradicionalRepository;
import com.gumeinteligenciacomercial.orcaja.infrastructure.repositories.entities.OrcamentoEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class OrcamentoDataProvider implements OrcamentoGateway {

    private final OrcamentoRepository repository;
    private final OrcamentoTradicionalRepository repositoryTradicional;
    private final String MENSAGEM_ERRO_CONSULTAR_POR_ID = "Erro ao consultar orçamento pelo seu id.";
    private final String MENSAGEM_ERRO_LISTAR_POR_USUARIO = "Erro ao listar orçamentos pelo usuário.";
    private final String MENSAMGEM_ERRO_SALVAR = "Erro ao salvar novo orçamento.";
    private final String MENSAGEN_ERRO_DELETAR = "Erro deletar orçamento pelo id.";

    @Override
    public Optional<Orcamento> consultarPorId(String idOrcamento) {
        Optional<OrcamentoEntity> orcamentoEntity;

        try {
            orcamentoEntity = repository.findById(idOrcamento);
        } catch (Exception ex) {
            log.error(MENSAGEM_ERRO_CONSULTAR_POR_ID, ex);
            throw new DataProviderException(MENSAGEM_ERRO_CONSULTAR_POR_ID, ex.getCause());
        }


        return orcamentoEntity.map(OrcamentoMapper::paraDomain);
    }

    @Override
    public Page<Orcamento> listarPorUsuario(String idUsuario, Pageable pageable) {
        Page<OrcamentoEntity> orcamentos;

        try {
            orcamentos = repository.findByIdUsuario(idUsuario, pageable);
        } catch (Exception ex) {
            log.error(MENSAGEM_ERRO_LISTAR_POR_USUARIO, ex);
            throw new DataProviderException(MENSAGEM_ERRO_LISTAR_POR_USUARIO, ex.getCause());
        }

        return orcamentos.map(OrcamentoMapper::paraDomain);
    }

    @Override
    public Orcamento salvar(Orcamento orcamento) {
        OrcamentoEntity orcamentoEntity = OrcamentoMapper.paraEntity(orcamento);

        try {
            orcamentoEntity = repository.save(orcamentoEntity);
        } catch (Exception ex) {
            log.error(MENSAMGEM_ERRO_SALVAR, ex);
            throw new DataProviderException(MENSAMGEM_ERRO_SALVAR, ex.getCause());
        }

        return OrcamentoMapper.paraDomain(orcamentoEntity);
    }

    @Override
    public void deletar(String id) {
        try {
            repository.deleteById(id);
        } catch (Exception ex) {
            log.error(MENSAGEN_ERRO_DELETAR, ex);
            throw new DataProviderException(MENSAGEN_ERRO_DELETAR, ex.getCause());
        }
    }
}
