package com.gumeinteligenciacomercial.orcaja.infrastructure.dataprovider;

import com.gumeinteligenciacomercial.orcaja.domain.Orcamento;
import com.gumeinteligenciacomercial.orcaja.infrastructure.exceptions.DataProviderException;
import com.gumeinteligenciacomercial.orcaja.infrastructure.mapper.OrcamentoMapper;
import com.gumeinteligenciacomercial.orcaja.infrastructure.repositories.OrcamentoRepository;
import com.gumeinteligenciacomercial.orcaja.infrastructure.repositories.OrcamentoTradicionalRepository;
import com.gumeinteligenciacomercial.orcaja.infrastructure.repositories.entities.OrcamentoEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
class OrcamentoDataProviderTest {
    private static final String ERR_CONSULT = "Erro ao consultar orçamento pelo seu id.";
    private static final String ERR_LISTAR  = "Erro ao listar orçamentos pelo usuário.";
    private static final String ERR_SALVAR  = "Erro ao salvar novo orçamento.";
    private static final String ERR_DELETAR = "Erro deletar orçamento pelo id.";

    @Mock
    private OrcamentoRepository repository;

    @Mock
    private OrcamentoTradicionalRepository repositoryTradicional;

    @InjectMocks
    private OrcamentoDataProvider provider;

    private OrcamentoEntity entityIn;
    private OrcamentoEntity entityOut;
    private Orcamento domainIn;
    private Orcamento domainOut;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        // mocks puros, conteúdo irrelevante para estas verificações
        entityIn  = mock(OrcamentoEntity.class);
        entityOut = mock(OrcamentoEntity.class);
        domainIn  = mock(Orcamento.class);
        domainOut = mock(Orcamento.class);
        pageable  = Pageable.unpaged();
    }


    @Test
    void consultarPorId_comSucesso_deveRetornarOptionalDomain() {
        String id = "abc123";
        given(repository.findById(id)).willReturn(Optional.of(entityIn));

        try (MockedStatic<OrcamentoMapper> ms = mockStatic(OrcamentoMapper.class)) {
            ms.when(() -> OrcamentoMapper.paraDomain(entityIn)).thenReturn(domainOut);

            Optional<Orcamento> opt = provider.consultarPorId(id);
            assertTrue(opt.isPresent());
            assertSame(domainOut, opt.get());

            then(repository).should().findById(id);
            ms.verify(() -> OrcamentoMapper.paraDomain(entityIn));
        }
    }

    @Test
    void consultarPorId_quandoNaoEncontrar_deveRetornarOptionalVazio() {
        String id = "nao-existe";
        given(repository.findById(id)).willReturn(Optional.empty());

        Optional<Orcamento> opt = provider.consultarPorId(id);
        assertTrue(opt.isEmpty());
        then(repository).should().findById(id);
    }

    @Test
    void consultarPorId_quandoRepositoryLancarErro_deveLancarDataProviderException() {
        given(repository.findById(anyString())).willThrow(new RuntimeException("fail-consulta"));

        DataProviderException ex = assertThrows(
                DataProviderException.class,
                () -> provider.consultarPorId("x")
        );
        assertEquals(ERR_CONSULT, ex.getMessage());
    }

    @Test
    void listarPorUsuario_comSucesso_deveRetornarPageDomain() {
        String userId = "user1";
        Page<OrcamentoEntity> pageEntity = mock(Page.class);
        Page<Orcamento>       pageDomain = new PageImpl<>(List.of(domainOut));

        given(repository.findByIdUsuario(userId, pageable)).willReturn(pageEntity);
        doReturn(pageDomain)
                .when(pageEntity)
                .map(any(Function.class));

        Page<Orcamento> result = provider.listarPorUsuario(userId, pageable);
        assertSame(pageDomain, result);

        then(repository).should().findByIdUsuario(userId, pageable);
        then(pageEntity).should().map(any());
    }

    @Test
    void listarPorUsuario_quandoRepositoryLancarErro_deveLancarDataProviderException() {
        given(repository.findByIdUsuario(anyString(), any(Pageable.class)))
                .willThrow(new RuntimeException("fail-listar"));

        DataProviderException ex = assertThrows(
                DataProviderException.class,
                () -> provider.listarPorUsuario("u", pageable)
        );
        assertEquals(ERR_LISTAR, ex.getMessage());
    }

    @Test
    void salvar_comSucesso_deveRetornarDomainSalvo() {
        try (MockedStatic<OrcamentoMapper> ms = mockStatic(OrcamentoMapper.class)) {
            ms.when(() -> OrcamentoMapper.paraEntity(domainIn))
                    .thenReturn(entityIn);

            when(repository.save(entityIn))
                    .thenReturn(entityOut);

            ms.when(() -> OrcamentoMapper.paraDomain(entityOut))
                    .thenReturn(domainOut);

            Orcamento result = provider.salvar(domainIn);
            assertSame(domainOut, result);

            ms.verify(() -> OrcamentoMapper.paraEntity(domainIn));
            then(repository).should().save(entityIn);
            ms.verify(() -> OrcamentoMapper.paraDomain(entityOut));
        }
    }

    @Test
    void salvar_quandoRepositoryLancarErro_deveLancarDataProviderException() {
        when(repository.save(entityIn))
                .thenThrow(new RuntimeException("fail-save"));

        try (MockedStatic<OrcamentoMapper> ms = mockStatic(OrcamentoMapper.class)) {
            ms.when(() -> OrcamentoMapper.paraEntity(domainIn))
                    .thenReturn(entityIn);

            DataProviderException ex = assertThrows(
                    DataProviderException.class,
                    () -> provider.salvar(domainIn),
                    "Salvamento falho deve disparar DataProviderException"
            );
            assertEquals(ERR_SALVAR, ex.getMessage());

            ms.verify(() -> OrcamentoMapper.paraEntity(domainIn));
            then(repository).should().save(entityIn);
        }
    }

    @Test
    void deletar_comSucesso_deveChamarRepository() {
        String id = UUID.randomUUID().toString();
        assertDoesNotThrow(() -> provider.deletar(id));
        then(repository).should().deleteById(id);
    }

    @Test
    void deletar_quandoRepositoryLancarErro_deveLancarDataProviderException() {
        String id = UUID.randomUUID().toString();
        willThrow(new RuntimeException("fail-del"))
                .given(repository).deleteById(id);

        DataProviderException ex = assertThrows(
                DataProviderException.class,
                () -> provider.deletar(id)
        );
        assertEquals(ERR_DELETAR, ex.getMessage());
        then(repository).should().deleteById(id);
    }
}