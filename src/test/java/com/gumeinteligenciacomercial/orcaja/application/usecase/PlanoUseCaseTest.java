package com.gumeinteligenciacomercial.orcaja.application.usecase;

import com.gumeinteligenciacomercial.orcaja.application.exceptions.PlanoNaoEncontradoException;
import com.gumeinteligenciacomercial.orcaja.application.gateway.PlanoGateway;
import com.gumeinteligenciacomercial.orcaja.domain.Plano;
import com.gumeinteligenciacomercial.orcaja.domain.TipoPlano;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlanoUseCaseTest {

    @Mock
    private PlanoGateway gateway;

    @InjectMocks
    private PlanoUseCase useCase;

    @Test
    void listar_deveRetornarListaQuandoGatewaySucesso() {
        Plano p1 = mock(Plano.class);
        Plano p2 = mock(Plano.class);
        when(gateway.listar()).thenReturn(List.of(p1, p2));

        List<Plano> out = useCase.listar();

        assertNotNull(out);
        assertEquals(2, out.size());
        assertSame(p1, out.get(0));
        assertSame(p2, out.get(1));
        verify(gateway, times(1)).listar();
        verifyNoMoreInteractions(gateway);
    }

    @Test
    void listar_devePropagarExcecaoQuandoGatewayFalha() {
        RuntimeException infra = new RuntimeException("erro repo");
        when(gateway.listar()).thenThrow(infra);

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> useCase.listar());
        assertSame(infra, thrown);
        verify(gateway, times(1)).listar();
        verifyNoMoreInteractions(gateway);
    }

    @Test
    void consultarPlanoPadrao_deveRetornarPlanoQuandoEncontrado() {
        Plano plano = mock(Plano.class);
        when(gateway.consultarPlanoPeloTipo(TipoPlano.PADRAO)).thenReturn(Optional.of(plano));

        Plano out = useCase.consultarPlanoPeloTipo(TipoPlano.PADRAO);

        assertNotNull(out);
        assertSame(plano, out);
        verify(gateway, times(1)).consultarPlanoPeloTipo(TipoPlano.PADRAO);
        verifyNoMoreInteractions(gateway);
    }

    @Test
    void consultarPlanoPeloTipo_deveLancarExcecaoQuandoNaoEncontrado() {
        when(gateway.consultarPlanoPeloTipo(TipoPlano.PADRAO)).thenReturn(Optional.empty());

        assertThrows(PlanoNaoEncontradoException.class, () -> useCase.consultarPlanoPeloTipo(TipoPlano.PADRAO));
        verify(gateway, times(1)).consultarPlanoPeloTipo(TipoPlano.PADRAO);
        verifyNoMoreInteractions(gateway);
    }

    @Test
    void consultarPlanoPeloTipo_devePropagarExcecaoQuandoGatewayFalha() {
        IllegalStateException infra = new IllegalStateException("falha");
        when(gateway.consultarPlanoPeloTipo(TipoPlano.PADRAO)).thenThrow(infra);

        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> useCase.consultarPlanoPeloTipo(TipoPlano.PADRAO));
        assertSame(infra, thrown);
        verify(gateway, times(1)).consultarPlanoPeloTipo(TipoPlano.PADRAO);
        verifyNoMoreInteractions(gateway);
    }

}