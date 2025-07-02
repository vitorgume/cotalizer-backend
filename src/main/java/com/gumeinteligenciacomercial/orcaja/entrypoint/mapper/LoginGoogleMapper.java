package com.gumeinteligenciacomercial.orcaja.entrypoint.mapper;

import com.gumeinteligenciacomercial.orcaja.domain.LoginGoogle;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.LoginGoogleDto;

public class LoginGoogleMapper {
    public static LoginGoogleDto paraDto(LoginGoogle domain) {
        return LoginGoogleDto.builder()
                .headers(domain.getHeaders())
                .httpStatus(domain.getHttpStatus())
                .build();
    }
}
