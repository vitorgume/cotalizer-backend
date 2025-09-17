package com.gumeinteligenciacomercial.orcaja.application.usecase.google;

import com.gumeinteligenciacomercial.orcaja.application.gateway.AuthTokenGateway;
import com.gumeinteligenciacomercial.orcaja.application.usecase.UsuarioUseCase;
import com.gumeinteligenciacomercial.orcaja.domain.TipoCadastro;
import com.gumeinteligenciacomercial.orcaja.domain.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;

@Component
public class GoogleOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final AuthTokenGateway tokenService;
    private final UsuarioUseCase usuarioUseCase;

    @Value("${google.redirect.menu.url}")
    private final String GOOGLE_REDIRECT_MENU_URL;

    @Value("${google.redirect.login.url}")
    private final String GOOGLE_REDIRECT_LOGIN_URL;

    @Value("${app.security.csrf.secure}")
    private final boolean SECURE;

    @Value("${app.security.csrf.sameSite}")
    private final String SAME_SITE;

    public GoogleOAuth2SuccessHandler(
            AuthTokenGateway tokenService,
            UsuarioUseCase usuarioUseCase,
            @Value("${google.redirect.menu.url}") String GOOGLE_REDIRECT_MENU_URL,
            @Value("${google.redirect.login.url}") String GOOGLE_REDIRECT_LOGIN_URL,
            @Value("${app.security.csrf.secure}") boolean SECURE,
            @Value("${app.security.csrf.sameSite}") String SAME_SITE
    ) {
        this.tokenService = tokenService;
        this.usuarioUseCase = usuarioUseCase;
        this.GOOGLE_REDIRECT_MENU_URL = GOOGLE_REDIRECT_MENU_URL;
        this.GOOGLE_REDIRECT_LOGIN_URL = GOOGLE_REDIRECT_LOGIN_URL;
        this.SECURE = SECURE;
        this.SAME_SITE = SAME_SITE;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        Usuario usuario = usuarioUseCase.consultarPorEmail(email);

        String refreshToken = tokenService.generateRefreshToken(email, usuario.getId());
        ResponseCookie refreshCookie = ResponseCookie.from("REFRESH_TOKEN", refreshToken)
                .httpOnly(true)
                .secure(SECURE)
                .sameSite(SAME_SITE)
                .path("/")
                .maxAge(Duration.ofDays(30))
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        String redirectUrl = usuario.getTipoCadastro().equals(TipoCadastro.GOOGLE)
                ? GOOGLE_REDIRECT_MENU_URL
                : GOOGLE_REDIRECT_LOGIN_URL;

        response.sendRedirect(redirectUrl);
    }
}
