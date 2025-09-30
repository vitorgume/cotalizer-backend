package com.gumeinteligenciacomercial.orcaja.entrypoint.mapper;

import com.gumeinteligenciacomercial.orcaja.domain.Orcamento;
import com.gumeinteligenciacomercial.orcaja.domain.StatusOrcamento;
import com.gumeinteligenciacomercial.orcaja.domain.Template;
import com.gumeinteligenciacomercial.orcaja.domain.TipoOrcamento;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.OrcamentoDto;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.TemplateDto;
import com.gumeinteligenciacomercial.orcaja.infrastructure.repositories.entities.TemplateEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

class OrcamentoMapperTest {

    private Orcamento orcamentoDomain;
    private OrcamentoDto orcamentoDto;
    private Page<Orcamento> orcamentos;

    @BeforeEach
    void setUp() {
        orcamentoDomain = Orcamento.builder()
                .id("id-teste")
                .conteudoOriginal("Conteudo teste")
                .orcamentoFormatado(Map.of())
                .urlArquivo("url teste")
                .dataCriacao(LocalDate.now())
                .titulo("Titulo teste")
                .usuarioId("Id usuario teste")
                .status(StatusOrcamento.PENDENTE)
                .tipoOrcamento(TipoOrcamento.TRADICIONAL)
                .valorTotal(BigDecimal.valueOf(100))
                .template(Template.builder().id("teste").nomeArquivo("teste").build())
                .build();

        orcamentoDto = OrcamentoDto.builder()
                .id("id-teste-2")
                .conteudoOriginal("Conteudo teste 2")
                .orcamentoFormatado(Map.of())
                .urlArquivo("url teste 2")
                .dataCriacao(LocalDate.now())
                .titulo("Titulo teste 2")
                .usuarioId("Id usuario teste 2")
                .status(StatusOrcamento.APROVADO)
                .tipoOrcamento(TipoOrcamento.TRADICIONAL)
                .valorTotal(BigDecimal.valueOf(110))
                .template(TemplateDto.builder().build().builder().id("teste").nomeArquivo("teste").build())
                .build();

        orcamentos = new PageImpl<>(List.of(
                Orcamento.builder()
                        .id("id-teste 3")
                        .conteudoOriginal("Conteudo teste 3")
                        .orcamentoFormatado(Map.of())
                        .urlArquivo("url teste 3")
                        .dataCriacao(LocalDate.now())
                        .titulo("Titulo teste 3")
                        .usuarioId("Id usuario teste 3")
                        .status(StatusOrcamento.PENDENTE)
                        .tipoOrcamento(TipoOrcamento.TRADICIONAL)
                        .valorTotal(BigDecimal.valueOf(130))
                        .template(Template.builder().id("teste").nomeArquivo("teste").build())
                        .build(),
                Orcamento.builder()
                        .id("id-teste 4")
                        .conteudoOriginal("Conteudo teste 4")
                        .orcamentoFormatado(Map.of())
                        .urlArquivo("url teste 4")
                        .dataCriacao(LocalDate.now())
                        .titulo("Titulo teste 4")
                        .usuarioId("Id usuario teste 4")
                        .status(StatusOrcamento.PENDENTE)
                        .tipoOrcamento(TipoOrcamento.TRADICIONAL)
                        .valorTotal(BigDecimal.valueOf(140))
                        .template(Template.builder().id("teste").nomeArquivo("teste").build())
                        .build(),
                Orcamento.builder()
                        .id("id-teste 5")
                        .conteudoOriginal("Conteudo teste 5")
                        .orcamentoFormatado(Map.of())
                        .urlArquivo("url teste 5")
                        .dataCriacao(LocalDate.now())
                        .titulo("Titulo teste 5")
                        .usuarioId("Id usuario teste 5")
                        .status(StatusOrcamento.PENDENTE)
                        .tipoOrcamento(TipoOrcamento.TRADICIONAL)
                        .valorTotal(BigDecimal.valueOf(150))
                        .template(Template.builder().id("teste").nomeArquivo("teste").build())
                        .build()
        ));
    }

    @Test
    void deveRetornarDto() {
        OrcamentoDto orcamentoTeste = OrcamentoMapper.paraDto(orcamentoDomain);

        Assertions.assertEquals(orcamentoDomain.getId(), orcamentoTeste.getId());
        Assertions.assertEquals(orcamentoDomain.getConteudoOriginal(), orcamentoTeste.getConteudoOriginal());
        Assertions.assertEquals(orcamentoDomain.getOrcamentoFormatado(), orcamentoTeste.getOrcamentoFormatado());
        Assertions.assertEquals(orcamentoDomain.getUrlArquivo(), orcamentoTeste.getUrlArquivo());
        Assertions.assertEquals(orcamentoDomain.getDataCriacao(), orcamentoTeste.getDataCriacao());
        Assertions.assertEquals(orcamentoDomain.getTitulo(), orcamentoTeste.getTitulo());
        Assertions.assertEquals(orcamentoDomain.getUsuarioId(), orcamentoTeste.getUsuarioId());
        Assertions.assertEquals(orcamentoDomain.getStatus(), orcamentoTeste.getStatus());
        Assertions.assertEquals(orcamentoDomain.getTipoOrcamento(), orcamentoTeste.getTipoOrcamento());
        Assertions.assertEquals(orcamentoDomain.getTemplate().getId(), orcamentoTeste.getTemplate().getId());
        Assertions.assertEquals(orcamentoDomain.getValorTotal(), orcamentoTeste.getValorTotal());
    }

    @Test
    void deveRetornarDomain() {
        Orcamento orcamentoTeste = OrcamentoMapper.paraDomain(orcamentoDto);

        Assertions.assertEquals(orcamentoDto.getId(), orcamentoTeste.getId());
        Assertions.assertEquals(orcamentoDto.getConteudoOriginal(), orcamentoTeste.getConteudoOriginal());
        Assertions.assertEquals(orcamentoDto.getOrcamentoFormatado(), orcamentoTeste.getOrcamentoFormatado());
        Assertions.assertEquals(orcamentoDto.getUrlArquivo(), orcamentoTeste.getUrlArquivo());
        Assertions.assertEquals(orcamentoDto.getDataCriacao(), orcamentoTeste.getDataCriacao());
        Assertions.assertEquals(orcamentoDto.getTitulo(), orcamentoTeste.getTitulo());
        Assertions.assertEquals(orcamentoDto.getUsuarioId(), orcamentoTeste.getUsuarioId());
        Assertions.assertEquals(orcamentoDto.getStatus(), orcamentoTeste.getStatus());
        Assertions.assertEquals(orcamentoDto.getTipoOrcamento(), orcamentoTeste.getTipoOrcamento());
        Assertions.assertEquals(orcamentoDto.getTemplate().getId(), orcamentoTeste.getTemplate().getId());
        Assertions.assertEquals(orcamentoDto.getValorTotal(), orcamentoTeste.getValorTotal());
    }

    @Test
    void deveRetornarPageDeDtos() {
        Page<OrcamentoDto> orcamentoTesteList = OrcamentoMapper.paraDtos(orcamentos);

        for (int i = 0; i < orcamentoTesteList.getContent().size(); i++) {
            validaOrcamentoDto(orcamentoTesteList.getContent().get(i), i);
        }
    }

    private void validaOrcamentoDto(OrcamentoDto orcamentoTeste, int index) {
        Assertions.assertEquals(orcamentos.getContent().get(index).getId(), orcamentoTeste.getId());
        Assertions.assertEquals(orcamentos.getContent().get(index).getConteudoOriginal(), orcamentoTeste.getConteudoOriginal());
        Assertions.assertEquals(orcamentos.getContent().get(index).getOrcamentoFormatado(), orcamentoTeste.getOrcamentoFormatado());
        Assertions.assertEquals(orcamentos.getContent().get(index).getUrlArquivo(), orcamentoTeste.getUrlArquivo());
        Assertions.assertEquals(orcamentos.getContent().get(index).getDataCriacao(), orcamentoTeste.getDataCriacao());
        Assertions.assertEquals(orcamentos.getContent().get(index).getTitulo(), orcamentoTeste.getTitulo());
        Assertions.assertEquals(orcamentos.getContent().get(index).getUsuarioId(), orcamentoTeste.getUsuarioId());
        Assertions.assertEquals(orcamentos.getContent().get(index).getStatus(), orcamentoTeste.getStatus());
        Assertions.assertEquals(orcamentos.getContent().get(index).getTipoOrcamento(), orcamentoTeste.getTipoOrcamento());
        Assertions.assertEquals(orcamentos.getContent().get(index).getTemplate().getId(), orcamentoTeste.getTemplate().getId());
        Assertions.assertEquals(orcamentos.getContent().get(index).getValorTotal(), orcamentoTeste.getValorTotal());
    }

}