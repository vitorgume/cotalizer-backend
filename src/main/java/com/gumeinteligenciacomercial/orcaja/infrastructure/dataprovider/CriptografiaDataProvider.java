package com.gumeinteligenciacomercial.orcaja.infrastructure.dataprovider;

import com.gumeinteligenciacomercial.orcaja.application.gateway.CriptografiaGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CriptografiaDataProvider implements CriptografiaGateway {

    private final PasswordEncoder passwordEncoder;

    @Override
    public String criptografar(String senha) {
        return passwordEncoder.encode(senha);
    }

    @Override
    public boolean validarSenha(String senha, String senhaRepresentante) {
        return passwordEncoder.matches(senha, senhaRepresentante);
    }
}
