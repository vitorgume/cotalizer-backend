package com.gumeinteligenciacomercial.orcaja.infrastructure.dataprovider;

import com.gumeinteligenciacomercial.orcaja.domain.Usuario;
import com.gumeinteligenciacomercial.orcaja.infrastructure.exceptions.DataProviderException;
import com.gumeinteligenciacomercial.orcaja.infrastructure.mapper.UsuarioMapper;
import com.gumeinteligenciacomercial.orcaja.infrastructure.repositories.UsuarioRepository;
import com.gumeinteligenciacomercial.orcaja.infrastructure.repositories.entities.UsuarioEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioDataProviderTest {

    private static final String ERR_SALVAR = "Erro ao salvar usuário.";
    private static final String ERR_CONSULT_ID = "Erro ao consultar usuário pelo seu id.";
    private static final String ERR_CONSULT_CPF = "Erro ao consultar usuário pelo seu cpf.";
    private static final String ERR_LISTAR = "Erro ao listar usuários.";
    private static final String ERR_CONSULT_EMAIL = "Erro ao consultar usuário pelo seu email.";

    @Mock
    private UsuarioRepository repository;

    @InjectMocks
    private UsuarioDataProvider provider;

    private UsuarioEntity entityIn;
    private UsuarioEntity entityOut;
    private Usuario domainIn;
    private Usuario domainOut;

    private final String TEST_ID = UUID.randomUUID().toString();
    private final String TEST_CPF = "123.456.789-00";
    private final String TEST_EMAIL = "foo@bar.com";

    @BeforeEach
    void setUp() {
        entityIn = mock(UsuarioEntity.class);
        entityOut = mock(UsuarioEntity.class);
        domainIn = mock(Usuario.class);
        domainOut = mock(Usuario.class);
    }

    @Test
    void salvarComSucessoDeveRetornarDomain() {
        try (MockedStatic<UsuarioMapper> ms = mockStatic(UsuarioMapper.class)) {
            ms.when(() -> UsuarioMapper.paraEntity(domainIn))
                    .thenReturn(entityIn);
            given(repository.save(entityIn)).willReturn(entityOut);
            ms.when(() -> UsuarioMapper.paraDomain(entityOut))
                    .thenReturn(domainOut);

            Usuario result = provider.salvar(domainIn);
            assertSame(domainOut, result);

            ms.verify(() -> UsuarioMapper.paraEntity(domainIn));
            then(repository).should().save(entityIn);
            ms.verify(() -> UsuarioMapper.paraDomain(entityOut));
        }
    }

    @Test
    void salvarQuandoRepositoryLancarErroDeveLancarDataProviderException() {
        when(repository.save(any())).thenThrow(new RuntimeException("fail-save"));
        try (MockedStatic<UsuarioMapper> ms = mockStatic(UsuarioMapper.class)) {
            ms.when(() -> UsuarioMapper.paraEntity(domainIn))
                    .thenReturn(entityIn);

            DataProviderException ex = assertThrows(
                    DataProviderException.class,
                    () -> provider.salvar(domainIn)
            );
            assertEquals(ERR_SALVAR, ex.getMessage());
            ms.verify(() -> UsuarioMapper.paraEntity(domainIn));
            then(repository).should().save(entityIn);
        }
    }

    @Test
    void consultarPorIdComSucessoDeveRetornarOptionalDomain() {
        given(repository.findById(TEST_ID)).willReturn(Optional.of(entityIn));
        try (MockedStatic<UsuarioMapper> ms = mockStatic(UsuarioMapper.class)) {
            ms.when(() -> UsuarioMapper.paraDomain(entityIn))
                    .thenReturn(domainOut);

            Optional<Usuario> opt = provider.consultarPorId(TEST_ID);
            assertTrue(opt.isPresent());
            assertSame(domainOut, opt.get());

            then(repository).should().findById(TEST_ID);
            ms.verify(() -> UsuarioMapper.paraDomain(entityIn));
        }
    }

    @Test
    void consultarPorIdQuandoNaoEncontrarDeveRetornarOptionalVazio() {
        given(repository.findById(TEST_ID)).willReturn(Optional.empty());

        Optional<Usuario> opt = provider.consultarPorId(TEST_ID);
        assertTrue(opt.isEmpty());
        then(repository).should().findById(TEST_ID);
    }

    @Test
    void consultarPorIdQuandoRepositoryLancarErroDeveLancarDataProviderException() {
        given(repository.findById(anyString()))
                .willThrow(new RuntimeException("fail-find"));
        DataProviderException ex = assertThrows(
                DataProviderException.class,
                () -> provider.consultarPorId(TEST_ID)
        );
        assertEquals(ERR_CONSULT_ID, ex.getMessage());
    }

    @Test
    void consultarPorEmailComSucessoDeveRetornarOptionalDomain() {
        given(repository.findByEmail(TEST_EMAIL)).willReturn(Optional.of(entityIn));
        try (MockedStatic<UsuarioMapper> ms = mockStatic(UsuarioMapper.class)) {
            ms.when(() -> UsuarioMapper.paraDomain(entityIn))
                    .thenReturn(domainOut);

            Optional<Usuario> opt = provider.consultarPorEmail(TEST_EMAIL);
            assertTrue(opt.isPresent());
            assertSame(domainOut, opt.get());

            then(repository).should().findByEmail(TEST_EMAIL);
            ms.verify(() -> UsuarioMapper.paraDomain(entityIn));
        }
    }

    @Test
    void consultarPorEmailQuandoNaoEncontrarDeveRetornarOptionalVazio() {
        given(repository.findByEmail(TEST_EMAIL)).willReturn(Optional.empty());

        Optional<Usuario> opt = provider.consultarPorEmail(TEST_EMAIL);
        assertTrue(opt.isEmpty());
        then(repository).should().findByEmail(TEST_EMAIL);
    }

    @Test
    void consultarPorEmailQuandoRepositoryLancarErroDeveLancarDataProviderException() {
        given(repository.findByEmail(anyString()))
                .willThrow(new RuntimeException("fail-email"));
        DataProviderException ex = assertThrows(
                DataProviderException.class,
                () -> provider.consultarPorEmail(TEST_EMAIL)
        );
        assertEquals(ERR_CONSULT_EMAIL, ex.getMessage());
        then(repository).should().findByEmail(TEST_EMAIL);
    }

    @Test
    void listarComSucessoDeveRetornarListaDeDomainsMapeados() {
        given(repository.findAll()).willReturn(java.util.List.of(entityIn, entityOut));

        try (MockedStatic<UsuarioMapper> ms = mockStatic(UsuarioMapper.class)) {
            ms.when(() -> UsuarioMapper.paraDomain(entityIn)).thenReturn(domainIn);
            ms.when(() -> UsuarioMapper.paraDomain(entityOut)).thenReturn(domainOut);

            java.util.List<Usuario> res = provider.listar();

            assertNotNull(res);
            assertEquals(2, res.size());
            assertSame(domainIn, res.get(0));
            assertSame(domainOut, res.get(1));

            then(repository).should().findAll();
            ms.verify(() -> UsuarioMapper.paraDomain(entityIn));
            ms.verify(() -> UsuarioMapper.paraDomain(entityOut));
        }
    }

    @Test
    void listarQuandoRepositoryLancarErroDeveLancarDataProviderException() {
        given(repository.findAll()).willThrow(new RuntimeException("fail-list"));

        DataProviderException ex = assertThrows(DataProviderException.class, () -> provider.listar());
        assertEquals(ERR_LISTAR, ex.getMessage());

        then(repository).should().findAll();
    }

    @Test
    void listar_quandoRepositoryExplode_lancaDataProviderException() {
        UsuarioRepository repo = mock(UsuarioRepository.class);
        when(repo.findAll()).thenThrow(new RuntimeException("boom"));
        var provider = new UsuarioDataProvider(repo);
        assertThrows(DataProviderException.class, provider::listar);
    }

    @Test
    void listar_sucesso_mapeiaParaDomain() {
        UsuarioRepository repo = mock(UsuarioRepository.class);
        UsuarioEntity e = mock(UsuarioEntity.class);
        when(repo.findAll()).thenReturn(List.of(e));
        try (MockedStatic<UsuarioMapper> ms = mockStatic(UsuarioMapper.class)) {
            Usuario u = Usuario.builder().id("x").build();
            ms.when(() -> UsuarioMapper.paraDomain(e)).thenReturn(u);
            var provider = new UsuarioDataProvider(repo);
            List<Usuario> out = provider.listar();
            assertEquals(1, out.size());
            assertEquals("x", out.get(0).getId());
        }
    }

}