package com.gumeinteligenciacomercial.orcaja.entrypoint.controller;

import com.gumeinteligenciacomercial.orcaja.application.usecase.LoginUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/auth/google")
@RequiredArgsConstructor
public class GoogleAuthController {

    private final LoginUseCase loginUseCase;


    @GetMapping("/success")
    public ResponseEntity<?> loginSuccess(@AuthenticationPrincipal OAuth2User user) {
        String email = user.getAttribute("email");
        String nome = user.getAttribute("name");

        // Aqui você pode salvar o usuário no banco se ainda não existir
        String jwt = loginUseCase.gerarTokenJwt(email);

        // Redirecionar com o token no front-end
        URI redirectUri = URI.create("http://localhost:3000/login/sucesso?token=" + jwt);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(redirectUri);

        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }
}
