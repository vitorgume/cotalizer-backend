package com.gumeinteligenciacomercial.orcaja.entrypoint.mapper;

import com.gumeinteligenciacomercial.orcaja.domain.Login;
import com.gumeinteligenciacomercial.orcaja.domain.LoginGoogle;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.LoginGoogleDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;

class LoginGoogleMapperTest {

    private LoginGoogle loginGoogleDomain;

    @BeforeEach
    void setUp() {
        loginGoogleDomain = LoginGoogle.builder()
                .headers(HttpHeaders.EMPTY)
                .httpStatus(HttpStatus.NOT_FOUND)
                .build();
    }

    @Test
    void deveRetornarDomain() {
        LoginGoogleDto resultado = LoginGoogleMapper.paraDto(loginGoogleDomain);

        Assertions.assertEquals(loginGoogleDomain.getHeaders(), resultado.getHeaders());
        Assertions.assertEquals(loginGoogleDomain.getHttpStatus(), resultado.getHttpStatus());
    }
}