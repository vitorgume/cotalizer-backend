package com.gumeinteligenciacomercial.orcaja.entrypoint.controller;

import com.gumeinteligenciacomercial.orcaja.application.usecase.LoginUseCase;
import com.gumeinteligenciacomercial.orcaja.application.usecase.RefreshSessionUseCase;
import com.gumeinteligenciacomercial.orcaja.application.usecase.UsuarioUseCase;
import com.gumeinteligenciacomercial.orcaja.domain.AuthResult;
import com.gumeinteligenciacomercial.orcaja.domain.RefreshResult;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.AcessTokenResponseDto;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.LoginDto;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.ResponseDto;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.UsuarioDto;
import com.gumeinteligenciacomercial.orcaja.entrypoint.mapper.LoginMapper;
import com.gumeinteligenciacomercial.orcaja.entrypoint.mapper.UsuarioMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class LoginController {

    private final LoginUseCase loginUseCase;
    private final RefreshSessionUseCase refreshSessionUseCase;
    private final UsuarioUseCase usuarioUseCase;
    private static final String REFRESH_COOKIE = "__Host-refresh";

    @PostMapping("/login")
    public ResponseEntity<ResponseDto<LoginDto>> login(@RequestBody LoginDto login,
                                                       HttpServletResponse res) {
        AuthResult result = loginUseCase.autenticar(LoginMapper.paraDomain(login));

        addRefreshCookie(res, result.getRefreshToken());

        LoginDto loginDto = LoginDto.builder()
                .usuarioId(result.getUsuario().getId())
                .token(result.getAccessToken())
                .build();

        return ResponseEntity.ok(new ResponseDto<>(loginDto));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ResponseDto<AcessTokenResponseDto>> refresh(
            @CookieValue(name = REFRESH_COOKIE, required = false) String refreshCookie,
            HttpServletResponse res
    ) {
        if (refreshCookie == null || refreshCookie.isBlank()) return ResponseEntity.status(401).build();

        RefreshResult result = refreshSessionUseCase.renovar(refreshCookie);
        addRefreshCookie(res, result.getNewRefreshToken());
        return ResponseEntity.ok(new ResponseDto<>(new AcessTokenResponseDto(result.getNewAccessToken())));
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
                .httpOnly(true).secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(Duration.ofDays(30))
                .build();
        res.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private void clearRefreshCookie(HttpServletResponse res) {
        ResponseCookie cookie = ResponseCookie.from(REFRESH_COOKIE, "")
                .httpOnly(true).secure(true)
                .sameSite("None").path("/").maxAge(0)
                .build();
        res.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
