package com.gumeinteligenciacomercial.orcaja.application.usecase.ia;

import com.gumeinteligenciacomercial.orcaja.application.exceptions.PromptNaoEncontradoException;
import com.gumeinteligenciacomercial.orcaja.application.gateway.PromptGateway;
import com.gumeinteligenciacomercial.orcaja.domain.Prompt;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PromptUseCaseTest {

    @Mock
    private PromptGateway gateway;

    @InjectMocks
    private PromptUseCase promptUseCase;

    @Captor
    private ArgumentCaptor<String> idCaptor;

    private Prompt buildPrompt(String id, String model, String conteudo) {
        return Prompt.builder()
                .id(id)
                .modelIa(model)
                .conteudo(conteudo)
                .build();
    }

    @Test
    void buscarPorIdAtivo_deveRetornarPromptQuandoEncontrado() {
        // arrange
        String id = "fd6ae793-f385-4d44-8b8f-b2571ebcf901";
        Prompt esperado = buildPrompt(id, "gpt-4o-mini", "SYSTEM_PROMPT");
        when(gateway.buscarPorIdAtivo(id)).thenReturn(Optional.of(esperado));

        // act
        Prompt resultado = promptUseCase.buscarPorIdAtivo(id);

        // assert
        assertNotNull(resultado);
        assertSame(esperado, resultado); // retorna exatamente o objeto do gateway
        verify(gateway, times(1)).buscarPorIdAtivo(idCaptor.capture());
        assertEquals(id, idCaptor.getValue());
        verifyNoMoreInteractions(gateway);
    }

    @Test
    void buscarPorIdAtivo_quandoNaoEncontrado_deveLancarPromptNaoEncontradoException() {
        // arrange
        String id = "inexistente";
        when(gateway.buscarPorIdAtivo(id)).thenReturn(Optional.empty());

        // act + assert
        assertThrows(PromptNaoEncontradoException.class,
                () -> promptUseCase.buscarPorIdAtivo(id));

        verify(gateway, times(1)).buscarPorIdAtivo(id);
        verifyNoMoreInteractions(gateway);
    }

    @Test
    void buscarPorIdAtivo_quandoGatewayLancaExcecao_devePropagarExcecao() {
        // arrange
        String id = "erro";
        RuntimeException infra = new RuntimeException("Falha no repositório");
        when(gateway.buscarPorIdAtivo(id)).thenThrow(infra);

        // act
        RuntimeException thrown = assertThrows(RuntimeException.class,
                () -> promptUseCase.buscarPorIdAtivo(id));

        // assert
        assertSame(infra, thrown); // garante que propagou a mesma exceção
        verify(gateway, times(1)).buscarPorIdAtivo(id);
        verifyNoMoreInteractions(gateway);
    }

    @Test
    void buscarPorIdAtivo_deveChamarGatewayUmaUnicaVezComIdCorreto() {
        // arrange
        String id = "123";
        when(gateway.buscarPorIdAtivo(anyString()))
                .thenReturn(Optional.of(buildPrompt(id, "gpt-4o", "SYS")));

        // act
        promptUseCase.buscarPorIdAtivo(id);

        // assert
        verify(gateway, times(1)).buscarPorIdAtivo(idCaptor.capture());
        assertEquals(id, idCaptor.getValue());
        verifyNoMoreInteractions(gateway);
    }

}