package com.gumeinteligenciacomercial.orcaja.infrastructure.dataprovider;

import com.gumeinteligenciacomercial.orcaja.domain.Plano;
import com.gumeinteligenciacomercial.orcaja.infrastructure.exceptions.DataProviderException;
import com.gumeinteligenciacomercial.orcaja.infrastructure.mapper.PlanoMapper;
import com.gumeinteligenciacomercial.orcaja.infrastructure.repositories.PlanoRepository;
import com.gumeinteligenciacomercial.orcaja.infrastructure.repositories.entities.PlanoEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
        PlanoEntity e1 = mock(PlanoEntity.class);
        PlanoEntity e2 = mock(PlanoEntity.class);
        when(repository.findAll()).thenReturn(Arrays.asList(e1, e2));

        Plano p1 = mock(Plano.class);
        Plano p2 = mock(Plano.class);

        try (MockedStatic<PlanoMapper> mocked = mockStatic(PlanoMapper.class)) {
            mocked.when(() -> PlanoMapper.paraDomain(e1)).thenReturn(p1);
            mocked.when(() -> PlanoMapper.paraDomain(e2)).thenReturn(p2);

            List<Plano> out = dataProvider.listar();

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
        RuntimeException infra = new RuntimeException("Falha Mongo");
        when(repository.findAll()).thenThrow(infra);

        DataProviderException ex = assertThrows(DataProviderException.class, () -> dataProvider.listar());

        assertEquals("Erro ao listar planos.", ex.getMessage());

        verify(repository, times(1)).findAll();
        verifyNoMoreInteractions(repository);
    }
}