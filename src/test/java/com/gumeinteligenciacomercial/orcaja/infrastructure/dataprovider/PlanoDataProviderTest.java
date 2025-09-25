package com.gumeinteligenciacomercial.orcaja.infrastructure.dataprovider;

import com.gumeinteligenciacomercial.orcaja.domain.Plano;
import com.gumeinteligenciacomercial.orcaja.entrypoint.mapper.PlanoMapper;
import com.gumeinteligenciacomercial.orcaja.infrastructure.repositories.PlanoRepository;
import com.gumeinteligenciacomercial.orcaja.infrastructure.repositories.entities.PlanoEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlanoDataProviderTest {

    @Mock
    private PlanoRepository repository;

    @InjectMocks
    private PlanoDataProvider dataProvider;

    @Test
    void listar_deveRetornarListaMapeada_quandoRepositorioSucesso() {
        // arrange
        PlanoEntity e1 = mock(PlanoEntity.class);
        PlanoEntity e2 = mock(PlanoEntity.class);
        when(repository.findAll()).thenReturn(Arrays.asList(e1, e2));

        Plano p1 = mock(Plano.class);
        Plano p2 = mock(Plano.class);

        try (MockedStatic<PlanoMapper> mocked = mockStatic(PlanoMapper.class)) {
            mocked.when(() -> PlanoMapper.paraDomain(e1)).thenReturn(p1);
            mocked.when(() -> PlanoMapper.paraDomain(e2)).thenReturn(p2);

            // act
            List<Plano> out = dataProvider.listar();

            // assert
            assertNotNull(out);
            assertEquals(2, out.size());
            assertSame(p1, out.get(0));
            assertSame(p2, out.get(1));

            verify(repository, times(1)).findAll();
            verifyNoMoreInteractions(repository);
        }
    }

    @Test
    void listar_deveLancarDataProviderException_quandoRepositorioFalha() {
        // arrange
        RuntimeException infra = new RuntimeException("Falha Mongo");
        when(repository.findAll()).thenThrow(infra);

        // act
        DataProviderException ex = assertThrows(DataProviderException.class, () -> dataProvider.listar());

        // assert
        assertEquals("Erro ao listar planos.", ex.getMessage());
        // No código original vocês fazem new DataProviderException(msg, ex.getCause()) — normalmente null.
        // Não dá para garantir a causa aqui, então checamos apenas a mensagem.
        verify(repository, times(1)).findAll();
        verifyNoMoreInteractions(repository);
    }

    @Test
    void consultarPlanoPadrao_deveRetornarOptionalComPlano_quandoEncontrado() {
        // arrange
        PlanoEntity e = mock(PlanoEntity.class);
        when(repository.findByPadraoTrue()).thenReturn(Optional.of(e));

        Plano p = mock(Plano.class);

        try (MockedStatic<PlanoMapper> mocked = mockStatic(PlanoMapper.class)) {
            mocked.when(() -> PlanoMapper.paraDomain(e)).thenReturn(p);

            // act
            Optional<Plano> out = dataProvider.consultarPlanoPadrao();

            // assert
            assertTrue(out.isPresent());
            assertSame(p, out.get());
            verify(repository, times(1)).findByPadraoTrue();
            verifyNoMoreInteractions(repository);
        }
    }

    @Test
    void consultarPlanoPadrao_deveRetornarOptionalVazio_quandoNaoEncontrado() {
        // arrange
        when(repository.findByPadraoTrue()).thenReturn(Optional.empty());

        try (MockedStatic<PlanoMapper> mocked = mockStatic(PlanoMapper.class)) {
            // Não deve chamar o mapper quando vazio; não configuramos nada.
            // act
            Optional<Plano> out = dataProvider.consultarPlanoPadrao();

            // assert
            assertTrue(out.isEmpty());
            verify(repository, times(1)).findByPadraoTrue();
            verifyNoMoreInteractions(repository);
            // opcionalmente poderíamos verificar que NENHUM paraDomain foi chamado:
            mocked.verifyNoInteractions();
        }
    }

    @Test
    void consultarPlanoPadrao_deveLancarDataProviderException_quandoRepositorioFalha() {
        // arrange
        RuntimeException infra = new RuntimeException("Falha Mongo");
        when(repository.findByPadraoTrue()).thenThrow(infra);

        // act
        DataProviderException ex = assertThrows(DataProviderException.class,
                () -> dataProvider.consultarPlanoPadrao());

        // assert
        assertEquals("Erro ao consultar plano padrão.", ex.getMessage());
        verify(repository, times(1)).findByPadraoTrue();
        verifyNoMoreInteractions(repository);
    }
}