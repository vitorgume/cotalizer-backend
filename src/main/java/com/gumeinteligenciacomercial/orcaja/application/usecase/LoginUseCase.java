package com.gumeinteligenciacomercial.orcaja.application.usecase;

import com.gumeinteligenciacomercial.orcaja.application.exceptions.CredenciasIncorretasException;
import com.gumeinteligenciacomercial.orcaja.application.gateway.AuthTokenGateway;
import com.gumeinteligenciacomercial.orcaja.application.gateway.LoginGateway;
import com.gumeinteligenciacomercial.orcaja.domain.AuthResult;
import com.gumeinteligenciacomercial.orcaja.domain.Login;
import com.gumeinteligenciacomercial.orcaja.domain.Usuario;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginUseCase {

    private final UsuarioUseCase usuarioUseCase;
    private final AuthTokenGateway tokenGateway;
    private final CriptografiaUseCase criptografiaUseCase;

    public AuthResult autenticar(Login login) {
        log.info("Autenticando usuário: {}", login.getEmail());
        Usuario usuario = usuarioUseCase.consultarPorEmail(login.getEmail());
        validaCredencias(usuario, login.getEmail(), login.getSenha());

        String access  = tokenGateway.generateAccessToken(usuario.getEmail(), usuario.getId(), null);
        String refresh = tokenGateway.generateRefreshToken(usuario.getEmail(), usuario.getId());

        log.info("Usuário autenticado. id={}", usuario.getId());
        return new AuthResult(usuario, access, refresh);
    }

    private void validaCredencias(Usuario usuario, String email, String senha) {
        if (usuario == null || !usuario.getEmail().equals(email) ||
                !criptografiaUseCase.validaSenha(senha, usuario.getSenha())) {
            throw new CredenciasIncorretasException();
        }
    }

}
