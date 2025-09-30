package com.gumeinteligenciacomercial.orcaja.infrastructure.dataprovider;

import com.gumeinteligenciacomercial.orcaja.application.gateway.TemplateGateway;
import com.gumeinteligenciacomercial.orcaja.domain.Template;
import com.gumeinteligenciacomercial.orcaja.infrastructure.exceptions.DataProviderException;
import com.gumeinteligenciacomercial.orcaja.infrastructure.mapper.TemplateMapper;
import com.gumeinteligenciacomercial.orcaja.infrastructure.repositories.TemplateRepository;
import com.gumeinteligenciacomercial.orcaja.infrastructure.repositories.entities.TemplateEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TemplateDataProviderTest {

    private static final String ERR_LISTAR_TODOS = "Erro ao listar todos os templates.";

    @Mock
    private TemplateRepository repository;

    @InjectMocks
    private TemplateDataProvider provider;

    private TemplateEntity entity1;
    private TemplateEntity entity2;
    private Template domain1;
    private Template domain2;

    @BeforeEach
    void setUp() {
        entity1 = mock(TemplateEntity.class);
        entity2 = mock(TemplateEntity.class);
        domain1 = mock(Template.class);
        domain2 = mock(Template.class);
    }

    @Test
    void listarTodos_comSucesso_deveRetornarListaMapeada() {
        // given
        given(repository.findAll()).willReturn(List.of(entity1, entity2));

        try (MockedStatic<TemplateMapper> ms = mockStatic(TemplateMapper.class)) {
            ms.when(() -> TemplateMapper.paraDomain(entity1)).thenReturn(domain1);
            ms.when(() -> TemplateMapper.paraDomain(entity2)).thenReturn(domain2);

            // when
            List<Template> res = provider.listarTodos();

            // then
            assertNotNull(res);
            assertEquals(2, res.size());
            assertSame(domain1, res.get(0));
            assertSame(domain2, res.get(1));

            verify(repository, times(1)).findAll();
            ms.verify(() -> TemplateMapper.paraDomain(entity1));
            ms.verify(() -> TemplateMapper.paraDomain(entity2));
        }
    }

    @Test
    void listarTodos_quandoRepositorioRetornaVazio_deveRetornarListaVazia() {
        // given
        given(repository.findAll()).willReturn(List.of());

        // when
        List<Template> res = provider.listarTodos();

        // then
        assertNotNull(res);
        assertTrue(res.isEmpty());
        verify(repository, times(1)).findAll();
    }

    @Test
    void listarTodos_quandoRepositorioLancarErro_deveLancarDataProviderException() {
        // given
        given(repository.findAll()).willThrow(new RuntimeException("boom"));

        // when
        DataProviderException ex = assertThrows(DataProviderException.class, () -> provider.listarTodos());

        // then
        assertEquals(ERR_LISTAR_TODOS, ex.getMessage());
        verify(repository, times(1)).findAll();
    }

    // Versões “construtor manual”, iguais ao padrão que você usa:

    @Test
    void listarTodos_sucesso_mapeiaParaDomain_usandoRepoManual() {
        TemplateRepository repo = mock(TemplateRepository.class);
        TemplateDataProvider sut = new TemplateDataProvider(repo);

        TemplateEntity e = mock(TemplateEntity.class);
        given(repo.findAll()).willReturn(List.of(e));

        try (MockedStatic<TemplateMapper> ms = mockStatic(TemplateMapper.class)) {
            Template t = Template.builder().id("tpl-1").build();
            ms.when(() -> TemplateMapper.paraDomain(e)).thenReturn(t);

            List<Template> out = sut.listarTodos();

            assertEquals(1, out.size());
            assertEquals("tpl-1", out.get(0).getId());
            verify(repo).findAll();
            ms.verify(() -> TemplateMapper.paraDomain(e));
        }
    }

    @Test
    void listarTodos_quandoRepoExplode_lancaDataProviderException_usandoRepoManual() {
        TemplateRepository repo = mock(TemplateRepository.class);
        TemplateDataProvider sut = new TemplateDataProvider(repo);

        when(repo.findAll()).thenThrow(new RuntimeException("fail"));
        DataProviderException ex = assertThrows(DataProviderException.class, sut::listarTodos);
        assertEquals(ERR_LISTAR_TODOS, ex.getMessage());
        verify(repo).findAll();
    }

}