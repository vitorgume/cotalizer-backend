package com.gumeinteligenciacomercial.orcaja.application.usecase;

import com.gumeinteligenciacomercial.orcaja.domain.Orcamento;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrcamentoUseCase {

    private final OrcamentoGateway gateway;
    private final IaUseCase iaUseCase;
    private final ArquivoUseCase arquivoUseCase;

    public Orcamento cadastrar(Orcamento orcamento) {
        log.info("Cadastrando novo orçamento. Orçamento: {}", orcamento);

        Map<String, Object> orcamentoFormatado = iaUseCase.gerarOrcamento(orcamento.getConteudoOriginal());
        String arquivoUrl = arquivoUseCase.salvarArquivo(orcamentoFormatado);
        orcamento.setOrcamentoFormatado(orcamentoFormatado);
        orcamento.setUrlArquivo(arquivoUrl);
        Orcamento orcamentoSalvo = gateway.salvar(orcamento);

        log.info("Orçamento cadastrado com sucesso. Orçamento salvo: {}", orcamentoSalvo);
        return orcamentoSalvo;
    }

    public Orcamento consultarPorId(UUID idOrcamento) {
        log.info("Consultando Orçamento pelo seu id. Id do orçamento: {}", idOrcamento);

        Optional<Orcamento> orcamento = gateway.consultarPorId(idOrcamento);

        if(orcamento.isEmpty()) {
            throw new OrcamentoNaoEncontrado();
        }

        log.info("Orçamento consultado com sucesso. Orçamento: {}", orcamento);

        return orcamento.get();
    }

    public Page<Orcamento> listarPorUsuario(UUID idUsuario, Pageable pageable) {
        Page<Orcamento> orcamentos = 
    }
}
