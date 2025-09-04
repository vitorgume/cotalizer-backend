package com.gumeinteligenciacomercial.orcaja.entrypoint.controller;

import com.gumeinteligenciacomercial.orcaja.application.usecase.LoginUseCase;
import com.gumeinteligenciacomercial.orcaja.application.usecase.RefreshSessionUseCase;
import com.gumeinteligenciacomercial.orcaja.application.usecase.UsuarioUseCase;
import com.gumeinteligenciacomercial.orcaja.domain.AuthResult;
import com.gumeinteligenciacomercial.orcaja.domain.RefreshResult;
import com.gumeinteligenciacomercial.orcaja.domain.Usuario;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.AcessTokenResponseDto;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.LoginDto;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.ResponseDto;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.UsuarioDto;
import com.gumeinteligenciacomercial.orcaja.entrypoint.mapper.LoginMapper;
import com.gumeinteligenciacomercial.orcaja.entrypoint.mapper.UsuarioMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/auth")
public class LoginController {

    private final LoginUseCase loginUseCase;
    private final RefreshSessionUseCase refreshSessionUseCase;
    private final UsuarioUseCase usuarioUseCase;
    private static final String REFRESH_COOKIE = "REFRESH_TOKEN";

    @Value("${app.security.csrf.secure}")
    private final boolean SECURE;

    @Value("${app.security.csrf.sameSite}")
    private final String SAME_SITE;

    public LoginController(
            LoginUseCase loginUseCase,
            RefreshSessionUseCase refreshSessionUseCase,
            UsuarioUseCase usuarioUseCase,
            @Value("${app.security.csrf.secure}") boolean SECURE,
            @Value("${app.security.csrf.sameSite}") String SAME_SITE
    ) {
        this.loginUseCase = loginUseCase;
        this.refreshSessionUseCase = refreshSessionUseCase;
        this.usuarioUseCase = usuarioUseCase;
        this.SECURE = SECURE;
        this.SAME_SITE = SAME_SITE;
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseDto<LoginDto>> login(@RequestBody LoginDto login,
                                                       HttpServletResponse res) {
        AuthResult result = loginUseCase.autenticar(LoginMapper.paraDomain(login));

        addRefreshCookie(res, result.getRefreshToken());

        LoginDto loginDto = LoginDto.builder()
                .usuarioId(result.getUsuario().getId())
                .token(result.getAccessToken())
                .refreshToken(result.getRefreshToken())
                .build();

        return ResponseEntity.ok(new ResponseDto<>(loginDto));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ResponseDto<AcessTokenResponseDto>> refresh(
            @CookieValue(name = REFRESH_COOKIE, required = false) String rtCookie,
            @RequestHeader(value = "X-Refresh-Token", required = false) String rtHeader,
            HttpServletResponse res
    ) {
        String refresh = (rtHeader != null && !rtHeader.isBlank()) ? rtHeader : rtCookie;
        if (refresh == null || refresh.isBlank()) return ResponseEntity.status(401).build();

        RefreshResult result = refreshSessionUseCase.renovar(refresh);

        // opcional: continuar setando cookie p/ Chrome/Firefox
        // addRefreshCookie(res, result.getNewRefreshToken());

        // >>> IMPORTANTE: retorne o novo RT no corpo para o modo "header" poder girar o token
        return ResponseEntity.ok(new ResponseDto<>(new AcessTokenResponseDto(
                result.getNewAccessToken(),
                result.getNewRefreshToken()
        )));
    }

    @GetMapping("/me")
    public ResponseEntity<ResponseDto<LoginDto>> me(Authentication auth) {
        if (auth == null) return ResponseEntity.status(401).build();
        UsuarioDto usuarioDto = UsuarioMapper.paraDto(usuarioUseCase.consultarPorEmail(auth.getName()));
        return ResponseEntity.ok(new ResponseDto<>(LoginDto.builder().usuarioId(usuarioDto.getId()).build()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse res) {
        clearRefreshCookie(res);
        return ResponseEntity.noContent().build();
    }

    private void addRefreshCookie(HttpServletResponse res, String token) {
        ResponseCookie cookie = ResponseCookie.from(REFRESH_COOKIE, token)
                .httpOnly(true).secure(SECURE)
                .sameSite(SAME_SITE)
                .path("/")
                .maxAge(Duration.ofDays(30))
                .build();
        res.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private void clearRefreshCookie(HttpServletResponse res) {
        ResponseCookie cookie = ResponseCookie.from(REFRESH_COOKIE, "")
                .httpOnly(true).secure(SECURE)
                .sameSite(SAME_SITE).path("/").maxAge(0)
                .build();
        res.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private void deleteLegacyCookies(HttpServletResponse res) {
        res.addHeader(HttpHeaders.SET_COOKIE,
                ResponseCookie.from("__Host-refresh","").httpOnly(true).secure(SECURE)
                        .sameSite(SAME_SITE).path("/").maxAge(0).build().toString());
        res.addHeader(HttpHeaders.SET_COOKIE,
                ResponseCookie.from("XSRF-TOKEN","").httpOnly(false).secure(SECURE)
                        .sameSite(SAME_SITE).path("/").maxAge(0).build().toString());
    }
}
