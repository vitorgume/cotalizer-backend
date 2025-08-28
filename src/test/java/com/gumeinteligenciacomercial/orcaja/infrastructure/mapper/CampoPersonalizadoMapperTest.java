package com.gumeinteligenciacomercial.orcaja.infrastructure.mapper;

import com.gumeinteligenciacomercial.orcaja.domain.CampoPersonalizado;
import com.gumeinteligenciacomercial.orcaja.infrastructure.repositories.entities.CampoPersonalizadoEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CampoPersonalizadoMapperTest {

    private CampoPersonalizado campoPersonalizadoDomain;
    private CampoPersonalizadoEntity campoPersonalizadoEntity;

    @BeforeEach
    void setUp() {
        campoPersonalizadoDomain = CampoPersonalizado.builder()
                .titulo("Titulo teste")
                .valor("Valor teste")
                .build();

        campoPersonalizadoEntity = CampoPersonalizadoEntity.builder()
                .titulo("Titulo teste 2")
                .valor("Valor teste 2")
                .build();
    }

    @Test
    void deveRetornarDomain() {
        CampoPersonalizado campoPersonalizadoTeste = CampoPersonalizadoMapper.paraDomain(campoPersonalizadoEntity);

        Assertions.assertEquals(campoPersonalizadoEntity.getTitulo(), campoPersonalizadoTeste.getTitulo());
        Assertions.assertEquals(campoPersonalizadoEntity.getValor(), campoPersonalizadoTeste.getValor());
    }

    @Test
    void deveRetornarEntity() {
        CampoPersonalizadoEntity campoPersonalizadoTeste = CampoPersonalizadoMapper.paraEntity(campoPersonalizadoDomain);

        Assertions.assertEquals(campoPersonalizadoDomain.getTitulo(), campoPersonalizadoTeste.getTitulo());
        Assertions.assertEquals(campoPersonalizadoDomain.getValor(), campoPersonalizadoTeste.getValor());
    }
}