package com.gumeinteligenciacomercial.orcaja.application.usecase;

import com.gumeinteligenciacomercial.orcaja.domain.Login;
import com.gumeinteligenciacomercial.orcaja.domain.LoginGoogle;
import com.gumeinteligenciacomercial.orcaja.domain.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.net.URI;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserUseCase extends DefaultOAuth2UserService {

    private final UsuarioUseCase usuarioUseCase;
    private final LoginUseCase loginUseCase;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User user = super.loadUser(userRequest);

        String email = user.getAttribute("email");
        String nome = user.getAttribute("name");

        Usuario novoUsuario = Usuario.builder().nome(nome).email(email).senha("test").build();

        usuarioUseCase.cadastrar(novoUsuario);

        return user;
    }

    public LoginGoogle logar(OAuth2User user) {
        String email = user.getAttribute("email");

        String jwt = loginUseCase.gerarTokenJwt(email);

        URI redirectUri = URI.create("http://localhost:5173/login/sucesso?token=" + jwt);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(redirectUri);

        return LoginGoogle.builder()
                .headers(headers)
                .httpStatus(HttpStatus.FOUND)
                .build();
    }
}
