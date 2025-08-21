package com.gumeinteligenciacomercial.orcaja.infrastructure.dataprovider;

import com.gumeinteligenciacomercial.orcaja.domain.OrcamentoTradicional;
import com.gumeinteligenciacomercial.orcaja.infrastructure.exceptions.DataProviderException;
import com.gumeinteligenciacomercial.orcaja.infrastructure.mapper.OrcamentoTradicionalMapper;
import com.gumeinteligenciacomercial.orcaja.infrastructure.repositories.OrcamentoTradicionalRepository;
import com.gumeinteligenciacomercial.orcaja.infrastructure.repositories.entities.OrcamentoTradicionalEntity;
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
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class OrcamentoTradicionalDataProviderTest {

    private static final String ERR_CADASTRAR = "Erro ao salvar orçamento tradicional.";
    private static final String ERR_CONSULTAR = "Erro ao consultar orçamento tradicional pelo seu id.";
    private static final String ERR_LISTAR = "Erro ao listar orçamentos tradicionais pelo usuário.";
    private static final String ERR_DELETAR = "Erro ao deletar orçamento tradicional.";

    @Mock
    private OrcamentoTradicionalRepository repository;

    @InjectMocks
    private OrcamentoTradicionalDataProvider provider;

    private OrcamentoTradicional novoDomain;
    private OrcamentoTradicionalEntity entityIn;
    private OrcamentoTradicionalEntity entityOut;
    private OrcamentoTradicional domainOut;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        novoDomain = mock(OrcamentoTradicional.class);
        entityIn = mock(OrcamentoTradicionalEntity.class);
        entityOut = mock(OrcamentoTradicionalEntity.class);
        domainOut = mock(OrcamentoTradicional.class);
        pageable = Pageable.unpaged();
    }

    @Test
    void salvarComSucessoDeveRetornarDomain() {
        try (MockedStatic<OrcamentoTradicionalMapper> ms = mockStatic(OrcamentoTradicionalMapper.class)) {
            ms.when(() -> OrcamentoTradicionalMapper.paraEntity(novoDomain))
                    .thenReturn(entityIn);
            given(repository.save(entityIn)).willReturn(entityOut);
            ms.when(() -> OrcamentoTradicionalMapper.paraDomain(entityOut))
                    .thenReturn(domainOut);

            OrcamentoTradicional result = provider.salvar(novoDomain);
            assertSame(domainOut, result);

            ms.verify(() -> OrcamentoTradicionalMapper.paraEntity(novoDomain));
            then(repository).should().save(entityIn);
            ms.verify(() -> OrcamentoTradicionalMapper.paraDomain(entityOut));
        }
    }

    @Test
    void salvarQuandoRepositoryLancarErroDeveLancarDataProviderException() {
        when(repository.save(any())).thenThrow(new RuntimeException("fail-save"));

        try (MockedStatic<OrcamentoTradicionalMapper> ms = mockStatic(OrcamentoTradicionalMapper.class)) {
            ms.when(() -> OrcamentoTradicionalMapper.paraEntity(novoDomain))
                    .thenReturn(entityIn);

            DataProviderException ex = assertThrows(
                    DataProviderException.class,
                    () -> provider.salvar(novoDomain)
            );
            assertEquals(ERR_CADASTRAR, ex.getMessage());

            ms.verify(() -> OrcamentoTradicionalMapper.paraEntity(novoDomain));
            then(repository).should().save(entityIn);
        }
    }


    @Test
    void consultarPorIdComSucesso_deveRetornarOptionalDomain() {
        String id = "abc";
        given(repository.findById(id)).willReturn(Optional.of(entityIn));

        try (MockedStatic<OrcamentoTradicionalMapper> ms = mockStatic(OrcamentoTradicionalMapper.class)) {
            ms.when(() -> OrcamentoTradicionalMapper.paraDomain(entityIn))
                    .thenReturn(domainOut);

            Optional<OrcamentoTradicional> opt = provider.consultarPorId(id);
            assertTrue(opt.isPresent());
            assertSame(domainOut, opt.get());

            then(repository).should().findById(id);
            ms.verify(() -> OrcamentoTradicionalMapper.paraDomain(entityIn));
        }
    }

    @Test
    void consultarPorIdQuandoNaoEncontrarDeveRetornarOptionalVazio() {
        String id = "nao-existe";
        given(repository.findById(id)).willReturn(Optional.empty());

        Optional<OrcamentoTradicional> opt = provider.consultarPorId(id);
        assertTrue(opt.isEmpty());
        then(repository).should().findById(id);
    }

    @Test
    void consultarPorIdQuandoRepositoryLancarErroDeveLancarDataProviderException() {
        given(repository.findById(anyString())).willThrow(new RuntimeException("fail-find"));

        DataProviderException ex = assertThrows(
                DataProviderException.class,
                () -> provider.consultarPorId("x")
        );
        assertEquals(ERR_CONSULTAR, ex.getMessage());
    }

    @Test
    void listarPorUsuarioComSucessoDeveRetornarPageDomain() {
        String userId = "u1";
        Page<OrcamentoTradicionalEntity> pageEntity = mock(Page.class);
        Page<OrcamentoTradicional> pageDomain = new PageImpl<>(List.of(domainOut));

        given(repository.findByIdUsuario(userId, pageable)).willReturn(pageEntity);
        // stub para mapear qualquer Function
        doReturn(pageDomain)
                .when(pageEntity)
                .map(any(Function.class));

        Page<OrcamentoTradicional> result = provider.listarPorUsuario(userId, pageable);
        assertSame(pageDomain, result);

        then(repository).should().findByIdUsuario(userId, pageable);
        verify(pageEntity).map(any(Function.class));
    }

    @Test
    void listarPorUsuarioQuandoRepositoryLancarErroDeveLancarDataProviderException() {
        given(repository.findByIdUsuario(anyString(), any(Pageable.class)))
                .willThrow(new RuntimeException("fail-list"));

        DataProviderException ex = assertThrows(
                DataProviderException.class,
                () -> provider.listarPorUsuario("u", pageable)
        );
        assertEquals(ERR_LISTAR, ex.getMessage());
    }

    @Test
    void deletarComSucessoDeveChamarRepository() {
        String id = UUID.randomUUID().toString();
        assertDoesNotThrow(() -> provider.deletar(id));
        then(repository).should().deleteById(id);
    }

    @Test
    void deletarQuandoRepositoryLancarErroDeveLancarDataProviderException() {
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