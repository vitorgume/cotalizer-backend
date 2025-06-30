package com.gumeinteligenciacomercial.orcaja.application.usecase;

import com.gumeinteligenciacomercial.orcaja.domain.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UsuarioUseCase usuarioUseCase;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User user = super.loadUser(userRequest);

        String email = user.getAttribute("email");
        String nome = user.getAttribute("name");

        Usuario novoUsuario = Usuario.builder().nome(nome).email(email).build();

        usuarioUseCase.cadastrar(novoUsuario);

        return user;
    }
}
