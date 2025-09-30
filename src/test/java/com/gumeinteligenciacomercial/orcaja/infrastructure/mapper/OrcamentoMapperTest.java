package com.gumeinteligenciacomercial.orcaja.infrastructure.mapper;

import com.gumeinteligenciacomercial.orcaja.domain.Orcamento;
import com.gumeinteligenciacomercial.orcaja.domain.StatusOrcamento;
import com.gumeinteligenciacomercial.orcaja.domain.Template;
import com.gumeinteligenciacomercial.orcaja.domain.TipoOrcamento;
import com.gumeinteligenciacomercial.orcaja.infrastructure.repositories.entities.OrcamentoEntity;
import com.gumeinteligenciacomercial.orcaja.infrastructure.repositories.entities.TemplateEntity;
import org.bson.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

class OrcamentoMapperTest {

    private Orcamento orcamentoDomain;
    private OrcamentoEntity orcamentoEntity;

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

        orcamentoEntity = OrcamentoEntity.builder()
                .id("id-teste-2")
                .conteudoOriginal("Conteudo teste 2")
                .orcamentoFormatado(new Document())
                .urlArquivo("url teste 2")
                .dataCriacao(LocalDate.now())
                .titulo("Titulo teste 2")
                .idUsuario("Id usuario teste 2")
                .status(StatusOrcamento.PENDENTE)
                .tipoOrcamento(TipoOrcamento.TRADICIONAL)
                .valorTotal(BigDecimal.valueOf(100))
                .template(TemplateEntity.builder().id("teste").nomeArquivo("teste").build())
                .build();
    }

    @Test
    void deveRetornarEntity() {
        OrcamentoEntity orcamentoTeste = OrcamentoMapper.paraEntity(orcamentoDomain);

        Assertions.assertEquals(orcamentoDomain.getId(), orcamentoTeste.getId());
        Assertions.assertEquals(orcamentoDomain.getConteudoOriginal(), orcamentoTeste.getConteudoOriginal());
        Assertions.assertEquals(orcamentoDomain.getOrcamentoFormatado(), orcamentoTeste.getOrcamentoFormatado());
        Assertions.assertEquals(orcamentoDomain.getUrlArquivo(), orcamentoTeste.getUrlArquivo());
        Assertions.assertEquals(orcamentoDomain.getDataCriacao(), orcamentoTeste.getDataCriacao());
        Assertions.assertEquals(orcamentoDomain.getTitulo(), orcamentoTeste.getTitulo());
        Assertions.assertEquals(orcamentoDomain.getUsuarioId(), orcamentoTeste.getIdUsuario());
        Assertions.assertEquals(orcamentoDomain.getStatus(), orcamentoTeste.getStatus());
        Assertions.assertEquals(orcamentoDomain.getTipoOrcamento(), orcamentoTeste.getTipoOrcamento());
        Assertions.assertEquals(orcamentoDomain.getTemplate().getId(), orcamentoTeste.getTemplate().getId());
        Assertions.assertEquals(orcamentoDomain.getValorTotal(), orcamentoTeste.getValorTotal());
    }

    @Test
    void deveRetornarDomain() {
        Orcamento orcamentoTeste = OrcamentoMapper.paraDomain(orcamentoEntity);

        Assertions.assertEquals(orcamentoEntity.getId(), orcamentoTeste.getId());
        Assertions.assertEquals(orcamentoEntity.getConteudoOriginal(), orcamentoTeste.getConteudoOriginal());
        Assertions.assertEquals(orcamentoEntity.getUrlArquivo(), orcamentoTeste.getUrlArquivo());
        Assertions.assertEquals(orcamentoEntity.getDataCriacao(), orcamentoTeste.getDataCriacao());
        Assertions.assertEquals(orcamentoEntity.getTitulo(), orcamentoTeste.getTitulo());
        Assertions.assertEquals(orcamentoEntity.getIdUsuario(), orcamentoTeste.getUsuarioId());
        Assertions.assertEquals(orcamentoEntity.getStatus(), orcamentoTeste.getStatus());
        Assertions.assertEquals(orcamentoEntity.getTipoOrcamento(), orcamentoTeste.getTipoOrcamento());
        Assertions.assertEquals(orcamentoEntity.getTemplate().getId(), orcamentoTeste.getTemplate().getId());
        Assertions.assertEquals(orcamentoEntity.getValorTotal(), orcamentoTeste.getValorTotal());

        Document esperado    = orcamentoEntity.getOrcamentoFormatado();
        Document resultado   = new Document(orcamentoTeste.getOrcamentoFormatado());
        Assertions.assertEquals(
                esperado,
                resultado,
                "O conteúdo do orcamentoFormatado deve ser o mesmo"
        );
    }
}