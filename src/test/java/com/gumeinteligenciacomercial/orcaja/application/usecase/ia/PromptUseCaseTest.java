package com.gumeinteligenciacomercial.orcaja.application.usecase.ia;

import com.gumeinteligenciacomercial.orcaja.application.gateway.PromptGateway;
import com.gumeinteligenciacomercial.orcaja.domain.Prompt;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.LinkedList;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PromptUseCaseTest {

    @Mock
    private PromptGateway gateway;

    @InjectMocks
    private PromptUseCase promptUseCase;

    @Test
    void buscarAtivo_comListaNaoVazia_retornaPrimeiroPrompt() {
        Prompt first = Prompt.builder()
                .id("Id teste")
                .modelIa("gpt-3.5")
                .conteudo("system-prompt")
                .build();
        Prompt second = Prompt.builder()
                .id("Id teste 2")
                .modelIa("gpt-4")
                .conteudo("other-prompt")
                .build();
        LinkedList<Prompt> list = new LinkedList<>();
        list.add(first);
        list.add(second);

        when(gateway.buscarAtivo()).thenReturn(list);

        Prompt result = promptUseCase.buscarAtivo();

        assertSame(first, result, "Deve retornar o primeiro Prompt da lista");
        verify(gateway, times(1)).buscarAtivo();
    }

    @Test
    void buscarAtivo_comListaVazia_deveLancarNoSuchElementException() {
        when(gateway.buscarAtivo()).thenReturn(new LinkedList<>());

        assertThrows(NoSuchElementException.class, () -> {
            promptUseCase.buscarAtivo();
        }, "Quando não houver nenhum Prompt ativo, deve propagar NoSuchElementException");

        verify(gateway, times(1)).buscarAtivo();
    }
}