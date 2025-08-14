package com.gumeinteligenciacomercial.orcaja.application.usecase;

import com.gumeinteligenciacomercial.orcaja.application.exceptions.OrcamentoNaoEncontradoException;
import com.gumeinteligenciacomercial.orcaja.application.gateway.OrcamentoGateway;
import com.gumeinteligenciacomercial.orcaja.application.usecase.ia.IaUseCase;
import com.gumeinteligenciacomercial.orcaja.domain.Orcamento;
import com.gumeinteligenciacomercial.orcaja.domain.TipoOrcamento;
import com.gumeinteligenciacomercial.orcaja.domain.Usuario;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrcamentoIaUseCase {

    private final OrcamentoGateway gateway;
    private final IaUseCase iaUseCase;

    public Orcamento cadastrar(Orcamento orcamento) {
        log.info("Cadastrando novo orçamento. Orçamento: {}", orcamento);

        Map<String, Object> orcamentoFormatado = iaUseCase.gerarOrcamento(orcamento.getConteudoOriginal());

        orcamentoFormatado = this.calculaValorTotal(orcamentoFormatado);

        orcamento.setOrcamentoFormatado(orcamentoFormatado);
        orcamento.setDataCriacao(LocalDate.now());
        orcamento.setTipoOrcamento(TipoOrcamento.IA);

        Object raw = orcamentoFormatado.get("valorTotal");
        BigDecimal valorTotal;
        if (raw instanceof BigDecimal) {
            valorTotal = (BigDecimal) raw;
        } else {
            valorTotal = new BigDecimal(raw.toString());
        }
        orcamento.setValorTotal(valorTotal);

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
        log.info("Deletando orçamento. Id: {}", id);
        this.consultarPorId(id);
        gateway.deletar(id);
        log.info("Deleção de orçamento realizado com sucesso.");
    }

    public Orcamento alterar(String idOrcamento, Orcamento novoOrcamento) {
        log.info("Alterando orçamento. Id: {}, Orçamento: {}", idOrcamento, novoOrcamento);

        Orcamento orcamento = this.consultarPorId(idOrcamento);

        orcamento.setDados(novoOrcamento);

        orcamento = gateway.salvar(orcamento);

        log.info("Orçamento alterado com sucesso. Orçamento: {}", orcamento);

        return orcamento;
    }

    private Map<String, Object> calculaValorTotal(Map<String, Object> orcamentoFormatado) {
        BigDecimal subtotal = BigDecimal.ZERO;
        Object itensObj = orcamentoFormatado.get("itens");
        if (itensObj instanceof List) {
            List<?> itens = (List<?>) itensObj;
            for (Object o : itens) {
                if (o instanceof Map) {
                    Map<?,?> item = (Map<?,?>) o;
                    BigDecimal quantidade = new BigDecimal(item.get("quantidade").toString());
                    BigDecimal valorUnitario = new BigDecimal(item.get("valor_unit").toString());
                    subtotal = subtotal.add(valorUnitario.multiply(quantidade));
                }
            }
        }

        BigDecimal descontoCalculado = BigDecimal.ZERO;
        Object descObj = orcamentoFormatado.get("desconto");
        if (descObj != null) {
            String descStr = descObj.toString().trim();
            if (descStr.endsWith("%")) {
                String perc = descStr.substring(0, descStr.length() - 1);
                BigDecimal frac = new BigDecimal(perc).divide(BigDecimal.valueOf(100));
                descontoCalculado = subtotal.multiply(frac);
            } else {
                descontoCalculado = new BigDecimal(descStr);
            }
        }

        BigDecimal valorTotal = subtotal.subtract(descontoCalculado);

        orcamentoFormatado.put("subtotal", subtotal);
        orcamentoFormatado.put("descontoCalculado", descontoCalculado);
        orcamentoFormatado.put("valorTotal", valorTotal);

        return orcamentoFormatado;
    }
}
