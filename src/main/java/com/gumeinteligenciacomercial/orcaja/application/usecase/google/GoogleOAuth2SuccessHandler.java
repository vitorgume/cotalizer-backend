package com.gumeinteligenciacomercial.orcaja.application.usecase.google;

import com.gumeinteligenciacomercial.orcaja.application.usecase.LoginUseCase;
import com.gumeinteligenciacomercial.orcaja.application.usecase.UsuarioUseCase;
import com.gumeinteligenciacomercial.orcaja.domain.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class GoogleOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final LoginUseCase loginUseCase;
    private final UsuarioUseCase usuarioUseCase;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        Usuario usuario = usuarioUseCase.consultarPorEmail(email);

        String token = loginUseCase.gerarTokenJwt(email);
        String redirectUrl;

        if (usuario.getCnpj() != null || usuario.getCpf() != null) {
            redirectUrl = "http://localhost:5173/menu?token=" + token;
        } else {
            redirectUrl = "http://localhost:5173/login/sucesso?token=" + token;
        }

        response.sendRedirect(redirectUrl);
    }
}
