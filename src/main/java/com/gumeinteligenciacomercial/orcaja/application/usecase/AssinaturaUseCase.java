package com.gumeinteligenciacomercial.orcaja.application.usecase;

import com.gumeinteligenciacomercial.orcaja.application.gateway.PagamentoGateway;
import com.gumeinteligenciacomercial.orcaja.domain.Assinatura;
import com.gumeinteligenciacomercial.orcaja.domain.Plano;
import com.gumeinteligenciacomercial.orcaja.domain.Usuario;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AssinaturaUseCase {

    private final PagamentoGateway gateway;
    private final UsuarioUseCase usuarioUseCase;

    public Assinatura criarAssinatura(Assinatura novaAssinatura) {
        log.info("Criando nova assinatura. Assinatura: {}", novaAssinatura);

        Assinatura assinatura = gateway.criarAssinatura(novaAssinatura);

        Usuario usuario = usuarioUseCase.consultarPorId(assinatura.getIdUsuario());
        usuario.setIdAssinatura(assinatura.getId());
        usuario.setPlano(Plano.PLUS);
        usuarioUseCase.alterar(usuario.getId(), usuario);

        log.info("Nova assinatura criada com sucesso. Nova assinatura: {}", assinatura);

        return assinatura;
    }
}
