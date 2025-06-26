package com.gumeinteligenciacomercial.orcaja.entrypoint.mapper;

import com.gumeinteligenciacomercial.orcaja.domain.Login;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.LoginDto;

public class LoginMapper {
    public static LoginDto paraDto(Login domain) {
        return LoginDto.builder()
                .email(domain.getEmail())
                .token(domain.getToken())
                .build();

    }

    public static Login paraDomain(LoginDto dto) {
        return Login.builder()
                .email(dto.getEmail())
                .senha(dto.getSenha())
                .build();
    }
}
