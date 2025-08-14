package com.gumeinteligenciacomercial.orcaja.application.usecase;

import com.gumeinteligenciacomercial.orcaja.application.exceptions.LimiteOrcamentosPlanoException;
import com.gumeinteligenciacomercial.orcaja.domain.Orcamento;
import com.gumeinteligenciacomercial.orcaja.domain.OrcamentoTradicional;
import com.gumeinteligenciacomercial.orcaja.domain.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrcamentosUseCase {

    private final UsuarioUseCase usuarioUseCase;
    private final OrcamentoIaUseCase orcamentoIaUseCase;
    private final OrcamentoTradicionalUseCase orcamentoTradicionalUseCase;

    public Orcamento cadastrarOrcamentoIa(Orcamento novoOrcamento) {
        Usuario usuario = usuarioUseCase.consultarPorId(novoOrcamento.getUsuarioId());

        this.validarPlanoUsuario(usuario);

        return orcamentoIaUseCase.cadastrar(novoOrcamento);
    }

    public OrcamentoTradicional cadastrarOrcamentoTradicional(OrcamentoTradicional novoOrcamento) {
        Usuario usuario = usuarioUseCase.consultarPorId(novoOrcamento.getIdUsuario());

        this.validarPlanoUsuario(usuario);

        return orcamentoTradicionalUseCase.cadastrar(novoOrcamento);
    }

    private void validarPlanoUsuario(Usuario usuario) {
        Page<Orcamento> orcamentosIas = orcamentoIaUseCase.listarPorUsuario(usuario.getId(), PageRequest.of(0, 10));
        Page<OrcamentoTradicional> orcamentoTradicionais = orcamentoTradicionalUseCase.listarPorUsuario(usuario.getId(), PageRequest.of(0, 10));

        if(usuario.getPlano().getCodigo() == 0) {
            if((orcamentosIas.getNumberOfElements() + orcamentoTradicionais.getNumberOfElements()) == usuario.getPlano().getLimiteOrcamentos() ) {
                throw new LimiteOrcamentosPlanoException();
            }
        } else if (usuario.getPlano().getCodigo() == 1) {
            if((orcamentosIas.getNumberOfElements() + orcamentoTradicionais.getNumberOfElements()) == usuario.getPlano().getLimiteOrcamentos()) {
                throw new LimiteOrcamentosPlanoException();
            }
        }
    }
}
