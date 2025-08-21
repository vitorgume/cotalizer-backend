package com.gumeinteligenciacomercial.orcaja.entrypoint.mapper;

import com.gumeinteligenciacomercial.orcaja.domain.CampoPersonalizado;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.CampoPersonalizadoDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CampoPersonalizadoMapperTest {

    private CampoPersonalizado campoPersonalizadoDomain;
    private CampoPersonalizadoDto campoPersonalizadoDto;

    @BeforeEach
    void setUp() {
        campoPersonalizadoDomain = CampoPersonalizado.builder()
                .titulo("Titulo teste")
                .valor("Valor teste")
                .build();

        campoPersonalizadoDto = CampoPersonalizadoDto.builder()
                .titulo("Titulo teste 2")
                .valor("Valor teste 2")
                .build();
    }

    @Test
    void deveRetornarDto() {
        CampoPersonalizadoDto campoPersonalizadoTeste = CampoPersonalizadoMapper.paraDto(campoPersonalizadoDomain);

        Assertions.assertEquals(campoPersonalizadoDomain.getTitulo(), campoPersonalizadoTeste.getTitulo());
        Assertions.assertEquals(campoPersonalizadoDomain.getValor(), campoPersonalizadoTeste.getValor());
    }

    @Test
    void deveRetornarDomain() {
        CampoPersonalizado campoPersonalizadoTeste = CampoPersonalizadoMapper.paraDomain(campoPersonalizadoDto);

        Assertions.assertEquals(campoPersonalizadoDto.getTitulo(), campoPersonalizadoTeste.getTitulo());
        Assertions.assertEquals(campoPersonalizadoDto.getValor(), campoPersonalizadoTeste.getValor());
    }
}