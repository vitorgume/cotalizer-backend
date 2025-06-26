package com.gumeinteligenciacomercial.orcaja.application.usecase;

import com.gumeinteligenciacomercial.orcaja.application.gateway.CriptografiaGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CriptografiaUseCase {

    private final CriptografiaGateway gateway;

    public String criptografar(String senha) {
        return gateway.criptografar(senha);
    }

    public boolean validaSenha(String senha, String senhaRepresentante) {
        return gateway.validarSenha(senha, senhaRepresentante);
    }
}
