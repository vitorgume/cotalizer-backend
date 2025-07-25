package com.gumeinteligenciacomercial.orcaja.application.usecase;

import com.gumeinteligenciacomercial.orcaja.application.gateway.OrcamentoTradicionalGateway;
import com.gumeinteligenciacomercial.orcaja.domain.OrcamentoTradicional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrcamentoTradicionalUseCase {

    private final OrcamentoTradicionalGateway gateway;

    public OrcamentoTradicional cadastrar(OrcamentoTradicional novoOrcamento) {
        log.info("Cadastrando novo orçamento tradicional. Novo orçamento: {}", novoOrcamento);
        OrcamentoTradicional orcamentoSalvo = gateway.salvar(novoOrcamento);

        log.info("Novo orçamento tradicional cadastrado com sucesso.");
        return orcamentoSalvo;
    }
}
