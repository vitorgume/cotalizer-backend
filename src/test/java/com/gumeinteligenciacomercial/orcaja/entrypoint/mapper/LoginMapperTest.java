package com.gumeinteligenciacomercial.orcaja.entrypoint.mapper;

import com.gumeinteligenciacomercial.orcaja.domain.Login;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.LoginDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginMapperTest {

    private Login loginDomain;
    private LoginDto loginDto;

    @BeforeEach
    void setUp() {
        loginDomain = Login.builder()
                .email("emailteste@gmail.com")
                .token("token teste")
                .usuarioId("usuario-id-teste")
                .build();

        loginDto = LoginDto.builder()
                .email("emailteste02@gmail.com")
                .senha("senhateste123")
                .build();
    }

    @Test
    void deveRetornarDto() {
        LoginDto loginResultado = LoginMapper.paraDto(loginDomain);

        Assertions.assertEquals(loginDomain.getEmail(), loginResultado.getEmail());
        Assertions.assertEquals(loginDomain.getSenha(), loginResultado.getSenha());
    }

    @Test
    void deveRetornarDomain() {
        Login loginResultado = LoginMapper.paraDomain(loginDto);

        Assertions.assertEquals(loginDto.getEmail(), loginResultado.getEmail());
        Assertions.assertEquals(loginDto.getToken(), loginResultado.getToken());
        Assertions.assertEquals(loginDto.getUsuarioId(), loginResultado.getUsuarioId());
    }
}