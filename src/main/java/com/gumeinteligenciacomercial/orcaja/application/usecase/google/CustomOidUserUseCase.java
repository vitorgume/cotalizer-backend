package com.gumeinteligenciacomercial.orcaja.application.usecase.google;

import com.gumeinteligenciacomercial.orcaja.application.exceptions.UsuarioNaoEncontradoException;
import com.gumeinteligenciacomercial.orcaja.application.usecase.UsuarioUseCase;
import com.gumeinteligenciacomercial.orcaja.domain.Plano;
import com.gumeinteligenciacomercial.orcaja.domain.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomOidUserUseCase implements OAuth2UserService<OidcUserRequest, OidcUser> {

    private final UsuarioUseCase usuarioUseCase;
    private final OidcUserService delegate = new OidcUserService();;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser user = delegate.loadUser(userRequest);

        String email = user.getEmail();
        String nome  = (String) user.getAttributes().getOrDefault("name", email);

        try {
            usuarioUseCase.consultarPorEmail(email);
        } catch (UsuarioNaoEncontradoException ex) {
            Usuario salvo = Usuario.builder()
                    .nome(nome)
                    .email(email)
                    .senha(UUID.randomUUID().toString())
                    .plano(Plano.GRATIS)
                    .build();
            usuarioUseCase.cadastrar(salvo);
        }

        return user;
    }
}
