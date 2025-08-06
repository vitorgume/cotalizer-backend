package com.gumeinteligenciacomercial.orcaja.application.usecase;

import com.gumeinteligenciacomercial.orcaja.application.exceptions.OrcamentoTradicionalNaoEncontradoException;
import com.gumeinteligenciacomercial.orcaja.application.gateway.OrcamentoTradicionalGateway;
import com.gumeinteligenciacomercial.orcaja.domain.OrcamentoTradicional;
import com.gumeinteligenciacomercial.orcaja.domain.TipoOrcamento;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrcamentoTradicionalUseCase {

    private final OrcamentoTradicionalGateway gateway;

    public OrcamentoTradicional cadastrar(OrcamentoTradicional novoOrcamento) {
        log.info("Cadastrando novo orçamento tradicional. Novo orçamento: {}", novoOrcamento);

        novoOrcamento.setValorTotal(BigDecimal.valueOf(this.calcularValorTotal(novoOrcamento)));
        novoOrcamento.setTipoOrcamento(TipoOrcamento.TRADICIONAL);
        novoOrcamento.setDataCriacao(LocalDate.now());

        OrcamentoTradicional orcamentoSalvo = gateway.salvar(novoOrcamento);

        log.info("Novo orçamento tradicional cadastrado com sucesso.");
        return orcamentoSalvo;
    }

    public OrcamentoTradicional consultarPorId(String id) {
        log.info("Consultando orçamento tradicional pelo seu id. Id: {}", id);

        Optional<OrcamentoTradicional> orcamentoTradicional = gateway.consultarPorId(id);

        if(orcamentoTradicional.isEmpty()) {
            throw new OrcamentoTradicionalNaoEncontradoException();
        }

        log.info("Orçamento tradicional consultado com sucesso. Orçamento: {}", orcamentoTradicional);

        return orcamentoTradicional.get();
    }

    public OrcamentoTradicional alterar(String id, OrcamentoTradicional orcamento) {
        log.info("Alterando dados de orçamento tradicional. Id: {}, Orçamento: {}", id, orcamento);

        OrcamentoTradicional orcamentoTradicional = this.consultarPorId(id);
        orcamentoTradicional.setDados(orcamento);
        orcamentoTradicional = gateway.salvar(orcamentoTradicional);

        log.info("Orçamento tradicional alterado com sucesso. Orçamento: {}", orcamentoTradicional);

        return orcamentoTradicional;
    }

    public Page<OrcamentoTradicional> listarPorUsuario(String idUsuario, Pageable pageable) {
        log.info("Listando orçamentos tradicionais pelo usuario. Id usuário: {}", idUsuario);

        Page<OrcamentoTradicional> orcamentoTradicionais = gateway.listarPorUsuario(idUsuario, pageable);

        log.info("Orçamentos tradicionais listados pelo usuário com sucesso. Orçamentos: {}", orcamentoTradicionais);

        return orcamentoTradicionais;
    }

    public void deletar(String id) {
        log.info("Deletando orçamento tradicional. Id: {}", id);

        this.consultarPorId(id);
        gateway.deletar(id);

        log.info("Orçamento tradicional deletado com sucesso.");
    }

    private Double calcularValorTotal(OrcamentoTradicional novoOrcamento) {
         return novoOrcamento.getProdutos().stream()
                .mapToDouble(
                        produto -> produto.getValor().multiply(BigDecimal.valueOf(produto.getQuantidade())).doubleValue()
                ).sum();
    }
}
