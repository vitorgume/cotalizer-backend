package com.gumeinteligenciacomercial.orcaja.application.usecase;

import com.gumeinteligenciacomercial.orcaja.application.exceptions.OrcamentoNaoEncontradoException;
import com.gumeinteligenciacomercial.orcaja.application.gateway.OrcamentoGateway;
import com.gumeinteligenciacomercial.orcaja.application.usecase.ia.IaUseCase;
import com.gumeinteligenciacomercial.orcaja.domain.Orcamento;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

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

        orcamento.setOrcamentoFormatado(orcamentoFormatado);
        orcamento.setDataCriacao(LocalDate.now());
        Orcamento orcamentoSalvo = gateway.salvar(orcamento);

        log.info("Orçamento cadastrado com sucesso. Orçamento salvo: {}", orcamentoSalvo);
        return orcamentoSalvo;
    }

    public Orcamento consultarPorId(String idOrcamento) {
        log.info("Consultando Orçamento pelo seu id. Id do orçamento: {}", idOrcamento);

        Optional<Orcamento> orcamento = gateway.consultarPorId(idOrcamento);

        if(orcamento.isEmpty()) {
            throw new OrcamentoNaoEncontradoException();
        }

        log.info("Orçamento consultado com sucesso. Orçamento: {}", orcamento);

        return orcamento.get();
    }

    public Page<Orcamento> listarPorUsuario(String idUsuario, Pageable pageable) {
        log.info("Listando orçamentos pelo usuário. Id do usuário: {}", idUsuario);

        Page<Orcamento> orcamentos = gateway.listarPorUsuario(idUsuario, pageable);

        log.info("Orçamentos listados com sucesso. Orçamentos: {}", orcamentos);

        return orcamentos;
    }

    public void deletar(String id) {
        this.consultarPorId(id);
        gateway.deletar(id);
    }
}
