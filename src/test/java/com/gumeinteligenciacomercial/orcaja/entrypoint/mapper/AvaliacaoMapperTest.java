package com.gumeinteligenciacomercial.orcaja.entrypoint.mapper;

import com.gumeinteligenciacomercial.orcaja.domain.Avaliacao;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.AvaliacaoDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AvaliacaoMapperTest {

    private AvaliacaoDto avaliacaoDto;
    private Avaliacao avaliacaoDomain;

    @BeforeEach
    void setUp() {
        avaliacaoDto = AvaliacaoDto.builder()
                .idUsuario("id-teste")
                .nota(2)
                .sugestaoMelhoria("melhoria teste")
                .motivoNota("motivo teste")
                .build();

        avaliacaoDomain = Avaliacao.builder()
                .idUsuario("id-teste 2")
                .nota(3)
                .sugestaoMelhoria("melhoria teste 2")
                .motivoNota("motivo nota teste 2")
                .build();
    }

    @Test
    void deveRetornarDomain() {
        Avaliacao avaliacaoTeste = AvaliacaoMapper.paraDomain(avaliacaoDto);

        Assertions.assertEquals(avaliacaoDto.getIdUsuario(), avaliacaoTeste.getIdUsuario());
        Assertions.assertEquals(avaliacaoDto.getNota(), avaliacaoTeste.getNota());
        Assertions.assertEquals(avaliacaoDto.getMotivoNota(), avaliacaoTeste.getMotivoNota());
        Assertions.assertEquals(avaliacaoDto.getSugestaoMelhoria(), avaliacaoTeste.getSugestaoMelhoria());
    }

    @Test
    void deveRetornarDto() {
        AvaliacaoDto avaliacaoTeste = AvaliacaoMapper.paraDto(avaliacaoDomain);

        Assertions.assertEquals(avaliacaoDomain.getIdUsuario(), avaliacaoTeste.getIdUsuario());
        Assertions.assertEquals(avaliacaoDomain.getNota(), avaliacaoTeste.getNota());
        Assertions.assertEquals(avaliacaoDomain.getMotivoNota(), avaliacaoTeste.getMotivoNota());
        Assertions.assertEquals(avaliacaoDomain.getSugestaoMelhoria(), avaliacaoTeste.getSugestaoMelhoria());
    }
}