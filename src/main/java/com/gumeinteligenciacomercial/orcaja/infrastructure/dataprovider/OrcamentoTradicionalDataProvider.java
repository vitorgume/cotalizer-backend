package com.gumeinteligenciacomercial.orcaja.infrastructure.dataprovider;

import com.gumeinteligenciacomercial.orcaja.application.gateway.OrcamentoTradicionalGateway;
import com.gumeinteligenciacomercial.orcaja.domain.OrcamentoTradicional;
import com.gumeinteligenciacomercial.orcaja.infrastructure.exceptions.DataProviderException;
import com.gumeinteligenciacomercial.orcaja.infrastructure.mapper.OrcamentoTradicionalMapper;
import com.gumeinteligenciacomercial.orcaja.infrastructure.repositories.OrcamentoTradicionalRepository;
import com.gumeinteligenciacomercial.orcaja.infrastructure.repositories.entities.OrcamentoTradicionalEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrcamentoTradicionalDataProvider implements OrcamentoTradicionalGateway {

    private final OrcamentoTradicionalRepository repository;
    private final String MENSAGEM_ERRO_CADASTRAR_ORCAMENTO_TRADICIONAL = "Erro ao salvar orçamento tradicional.";
    private final String MENSAGEM_ERRO_CONSULTAR_POR_ID = "Erro ao consultar orçamento tradicional pelo seu id.";
    private final String MENSAGEM_ERRO_LISTAR_POR_USUARIO = "Erro ao listar orçamentos tradicionais pelo usuário.";
    private final String MENSAGEM_ERRO_DELETAR = "Erro ao deletar orçamento tradicional.";

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

    @Override
    public Optional<OrcamentoTradicional> consultarPorId(String id) {
        Optional<OrcamentoTradicionalEntity> orcamentoTradicional;

        try {
            orcamentoTradicional = repository.findById(id);
        } catch (Exception ex) {
            log.error(MENSAGEM_ERRO_CONSULTAR_POR_ID, ex);
            throw new DataProviderException(MENSAGEM_ERRO_CONSULTAR_POR_ID, ex.getCause());
        }

        return orcamentoTradicional.map(OrcamentoTradicionalMapper::paraDomain);
    }

    @Override
    public Page<OrcamentoTradicional> listarPorUsuario(String idUsuario, Pageable pageable) {
        Page<OrcamentoTradicionalEntity> orcamentos;

        try {
            orcamentos = repository.findByIdUsuario(idUsuario, pageable);
        } catch (Exception ex) {
            log.error(MENSAGEM_ERRO_LISTAR_POR_USUARIO, ex);
            throw new DataProviderException(MENSAGEM_ERRO_LISTAR_POR_USUARIO, ex.getCause());
        }

        return orcamentos.map(OrcamentoTradicionalMapper::paraDomain);
    }

    @Override
    public void deletar(String id) {
        try {
            repository.deleteById(id);
        } catch (Exception ex) {
            log.error(MENSAGEM_ERRO_DELETAR, ex);
            throw new DataProviderException(MENSAGEM_ERRO_DELETAR, ex.getCause());
        }
    }
}
