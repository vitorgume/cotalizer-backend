package com.gumeinteligenciacomercial.orcaja.application.usecase.google;

import com.gumeinteligenciacomercial.orcaja.application.exceptions.UsuarioNaoEncontradoException;
import com.gumeinteligenciacomercial.orcaja.application.usecase.UsuarioUseCase;
import com.gumeinteligenciacomercial.orcaja.domain.Plano;
import com.gumeinteligenciacomercial.orcaja.domain.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class CustomOAuth2UserUseCase implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UsuarioUseCase usuarioUseCase;
    private final OAuth2UserService<OAuth2UserRequest,OAuth2User> delegate;

    public CustomOAuth2UserUseCase(
            UsuarioUseCase usuarioUseCase,
            @Qualifier("defaultOauth2UserService")
            OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate
    ) {
        this.usuarioUseCase = usuarioUseCase;
        this.delegate     = delegate;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User user = delegate.loadUser(userRequest);

        String email = user.getAttribute("email");
        String nome  = user.getAttribute("name");
        try {
            usuarioUseCase.consultarPorEmail(email);
        } catch (UsuarioNaoEncontradoException ex) {
            Usuario novo = Usuario.builder()
                    .nome(nome)
                    .email(email)
                    .senha("test")
                    .plano(Plano.GRATIS)
                    .build();
            usuarioUseCase.cadastrar(novo);
        }

        return user;
    }
}
