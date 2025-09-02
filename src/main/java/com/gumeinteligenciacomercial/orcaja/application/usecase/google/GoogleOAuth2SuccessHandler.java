package com.gumeinteligenciacomercial.orcaja.application.usecase.google;

import com.gumeinteligenciacomercial.orcaja.application.gateway.AuthTokenGateway;
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

    private final AuthTokenGateway tokenService;
    private final UsuarioUseCase usuarioUseCase;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        Usuario usuario = usuarioUseCase.consultarPorEmail(email);

        String token = tokenService.generateAccessToken(email, usuario.getId(), null);
        String redirectUrl;

        if (usuario.getCnpj() != null || usuario.getCpf() != null) {
            redirectUrl = "https://cotalizer-frontend.onrender.com/menu?token=" + token;
        } else {
            redirectUrl = "https://cotalizer-frontend.onrender.com/login/sucesso?token=" + token;
        }

        response.sendRedirect(redirectUrl);
    }
}
