package com.gumeinteligenciacomercial.orcaja.entrypoint.controller;

import com.gumeinteligenciacomercial.orcaja.application.usecase.CustomOAuth2UserUseCase;
import com.gumeinteligenciacomercial.orcaja.domain.LoginGoogle;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.LoginDto;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.LoginGoogleDto;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.ResponseDto;
import com.gumeinteligenciacomercial.orcaja.entrypoint.mapper.LoginGoogleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/google")
@RequiredArgsConstructor
public class GoogleAuthController {

    private final CustomOAuth2UserUseCase useCase;

    @GetMapping("/success")
    public ResponseEntity<ResponseDto<LoginGoogleDto>> loginSuccess(@AuthenticationPrincipal OAuth2User user) {
        LoginGoogleDto resultado = LoginGoogleMapper.paraDto(useCase.logar(user));
        ResponseDto<LoginGoogleDto> response = new ResponseDto<>(resultado);
        return ResponseEntity.ok(response);
    }
}
