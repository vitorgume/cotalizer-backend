package com.gumeinteligenciacomercial.orcaja.application.usecase;

import com.gumeinteligenciacomercial.orcaja.application.exceptions.CredenciasIncorretasException;
import com.gumeinteligenciacomercial.orcaja.application.gateway.LoginGateway;
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
    private final LoginGateway gateway;
    private final CriptografiaUseCase criptografiaUseCase;

    public Login autenticar(Login login) {
        log.info("Autenticando usuário. Dados login: {}", login);
        Usuario usuario = usuarioUseCase.consultarPorEmail(login.getEmail());
        this.validaCredencias(usuario, login.getEmail(), login.getSenha());
        String token = gateway.generateToken(login.getEmail());

        log.info("Usuário autenticado com sucesso. Usuario: {}", usuario);

        return Login.builder()
                .token(token)
                .email(usuario.getEmail())
                .build();
    }

    private void validaCredencias(Usuario usuario, String email, String senha) {
        if(!usuario.getEmail().equals(email) || !criptografiaUseCase.validaSenha(senha, usuario.getSenha())) {
            throw new CredenciasIncorretasException();
        }
    }

}
