package com.gumeinteligenciacomercial.orcaja.application.usecase;

import com.gumeinteligenciacomercial.orcaja.application.gateway.TemplateGateway;
import com.gumeinteligenciacomercial.orcaja.domain.Template;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class TemplateUseCaseTest {

    @Mock
    private TemplateGateway gateway;

    @InjectMocks
    private TemplateUseCase useCase;

    private Template t1;
    private Template t2;

    @BeforeEach
    void setUp() {
        t1 = mock(Template.class);
        t2 = mock(Template.class);
    }

    @Test
    void listarTodos_deveRetornarListaDoGateway() {
        // given
        given(gateway.listarTodos()).willReturn(List.of(t1, t2));

        // when
        List<Template> out = useCase.listarTodos();

        // then
        assertNotNull(out);
        assertEquals(2, out.size());
        assertSame(t1, out.get(0));
        assertSame(t2, out.get(1));
        then(gateway).should(times(1)).listarTodos();
    }

    @Test
    void listarTodos_quandoVazio_deveRetornarListaVazia() {
        // given
        given(gateway.listarTodos()).willReturn(List.of());

        // when
        List<Template> out = useCase.listarTodos();

        // then
        assertNotNull(out);
        assertTrue(out.isEmpty());
        then(gateway).should(times(1)).listarTodos();
    }

    @Test
    void listarTodos_quandoGatewayLancarErro_devePropagar() {
        // given
        RuntimeException boom = new RuntimeException("falha gateway");
        given(gateway.listarTodos()).willThrow(boom);

        // when / then
        RuntimeException ex = assertThrows(RuntimeException.class, () -> useCase.listarTodos());
        assertSame(boom, ex);
        then(gateway).should(times(1)).listarTodos();
    }

}