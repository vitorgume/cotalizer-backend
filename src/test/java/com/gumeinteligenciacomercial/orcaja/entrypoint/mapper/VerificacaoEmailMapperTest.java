package com.gumeinteligenciacomercial.orcaja.entrypoint.mapper;

import com.gumeinteligenciacomercial.orcaja.domain.VerificacaoEmail;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.VerificacaoEmailDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class VerificacaoEmailMapperTest {

    private VerificacaoEmail verificacaoEmail;
    private VerificacaoEmailDto verificacaoEmailDto;

    @BeforeEach
    void setUp() {
        verificacaoEmail = VerificacaoEmail.builder()
                .email("emailteste@gmail.com")
                .codigo("codigo-123")
                .build();

        verificacaoEmailDto = VerificacaoEmailDto.builder()
                .email("emailteste2@gmail.com")
                .codigo("codigo-321")
                .build();
    }

    @Test
    void deveRetornarDomain() {
        VerificacaoEmail verificacaoEmailTeste = VerificacaoEmailMapper.paraDomain(verificacaoEmailDto);

        Assertions.assertEquals(verificacaoEmailDto.getEmail(), verificacaoEmailTeste.getEmail());
        Assertions.assertEquals(verificacaoEmailDto.getCodigo(), verificacaoEmailTeste.getCodigo());
    }

    @Test
    void deveRetornarDto() {
        VerificacaoEmailDto verificacaoEmailTeste = VerificacaoEmailMapper.paraDto(verificacaoEmail);

        Assertions.assertEquals(verificacaoEmail.getEmail(), verificacaoEmailTeste.getEmail());
        Assertions.assertEquals(verificacaoEmail.getCodigo(), verificacaoEmailTeste.getCodigo());
    }
}