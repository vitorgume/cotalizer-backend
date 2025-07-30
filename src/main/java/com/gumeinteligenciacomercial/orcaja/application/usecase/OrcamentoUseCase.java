package com.gumeinteligenciacomercial.orcaja.application.usecase;

import com.gumeinteligenciacomercial.orcaja.application.exceptions.LimiteOrcamentosPlano;
import com.gumeinteligenciacomercial.orcaja.application.exceptions.OrcamentoNaoEncontradoException;
import com.gumeinteligenciacomercial.orcaja.application.gateway.OrcamentoGateway;
import com.gumeinteligenciacomercial.orcaja.application.usecase.ia.IaUseCase;
import com.gumeinteligenciacomercial.orcaja.domain.Orcamento;
import com.gumeinteligenciacomercial.orcaja.domain.OrcamentoTradicional;
import com.gumeinteligenciacomercial.orcaja.domain.TipoOrcamento;
import com.gumeinteligenciacomercial.orcaja.domain.Usuario;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.OrcamentoTradicionalDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrcamentoUseCase {

    private final OrcamentoGateway gateway;
    private final IaUseCase iaUseCase;
    private final UsuarioUseCase usuarioUseCase;

    public Orcamento cadastrar(Orcamento orcamento) {
        log.info("Cadastrando novo orçamento. Orçamento: {}", orcamento);

        this.validarPlanoUsuario(orcamento.getUsuarioId());

        Map<String, Object> orcamentoFormatado = iaUseCase.gerarOrcamento(orcamento.getConteudoOriginal());

        orcamento.setOrcamentoFormatado(orcamentoFormatado);
        orcamento.setDataCriacao(LocalDate.now());
        orcamento.setTipoOrcamento(TipoOrcamento.IA);
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

    private void validarPlanoUsuario(String usuarioId) {
        Usuario usuario = usuarioUseCase.consultarPorId(usuarioId);

        if(usuario.getPlano().getCodigo() == 0) {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Orcamento> orcamentos = this.listarPorUsuario(usuarioId, pageable);

            if(orcamentos.getSize() == usuario.getPlano().getLimiteOrcamentos()) {
                throw new LimiteOrcamentosPlano();
            }
        } else if (usuario.getPlano().getCodigo() == 1) {
            Pageable pageable = PageRequest.of(0, 150);
            Page<Orcamento> orcamentos = this.listarPorUsuario(usuarioId, pageable);

            if(orcamentos.getSize() == usuario.getPlano().getLimiteOrcamentos()) {
                throw new LimiteOrcamentosPlano();
            }
        }
    }
}
