package com.gumeinteligenciacomercial.orcaja.application.usecase.ia;

import com.gumeinteligenciacomercial.orcaja.application.exceptions.ErroEnviarParaIaException;
import com.gumeinteligenciacomercial.orcaja.application.gateway.IaGateway;
import com.gumeinteligenciacomercial.orcaja.application.usecase.ia.dto.OpenIaResponseDto;
import com.gumeinteligenciacomercial.orcaja.application.usecase.ia.dto.PromptDto;
import com.gumeinteligenciacomercial.orcaja.domain.Prompt;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IaUseCaseTest {

    @Mock
    private PromptUseCase promptUseCase;

    @Mock
    private IaGateway gateway;

    @InjectMocks
    private IaUseCase iaUseCase;

    @Captor
    private ArgumentCaptor<PromptDto> promptDtoCaptor;

    @Test
    void gerarOrcamentoDeveChamarGatewayComPromptCorretoERetornarMapa() {
        String conteudoOriginal = "Quero 3 canetas azuis";
        Prompt promptDomain = Prompt.builder()
                .id("fd6ae793-f385-4d44-8b8f-b2571ebcf901")
                .modelIa("gpt-3.5")
                .conteudo("system-prompt")
                .build();
        when(promptUseCase.buscarPorIdAtivo("fd6ae793-f385-4d44-8b8f-b2571ebcf901")).thenReturn(promptDomain);

        OpenIaResponseDto responseIa = mock(OpenIaResponseDto.class);
        OpenIaResponseDto.Choice choice = mock(OpenIaResponseDto.Choice.class);
        OpenIaResponseDto.Message message = mock(OpenIaResponseDto.Message.class);
        LinkedList<OpenIaResponseDto.Choice> choices = new LinkedList<>();
        choices.add(choice);

        when(responseIa.getChoices()).thenReturn(choices);
        when(choice.getMessage()).thenReturn(message);
        String jsonContent = """
            {
              "produto": "Caneta",
              "quantidade": 3
            }
            """;
        when(message.getContent()).thenReturn(jsonContent);
        when(gateway.enviarMensagem(any(PromptDto.class))).thenReturn(responseIa);

        Map<String, Object> resultado = iaUseCase.gerarOrcamento(conteudoOriginal);

        verify(promptUseCase, times(1)).buscarPorIdAtivo("fd6ae793-f385-4d44-8b8f-b2571ebcf901");
        verify(gateway, times(1)).enviarMensagem(promptDtoCaptor.capture());
        PromptDto usado = promptDtoCaptor.getValue();
        assertEquals(promptDomain.getModelIa(), usado.getModel());
        assertEquals(2, usado.getMessages().size());
        assertEquals("system", usado.getMessages().get(0).getRole());
        assertEquals(promptDomain.getConteudo(), usado.getMessages().get(0).getContent());
        assertEquals("user", usado.getMessages().get(1).getRole());
        assertEquals(conteudoOriginal, usado.getMessages().get(1).getContent());

        assertEquals("Caneta",    resultado.get("produto"));
        assertEquals(3,           ((Number) resultado.get("quantidade")).intValue());
    }

    @Test
    void gerarOrcamentoQuandoJsonInvalidoDeveLancarConversaoJsonException() {
        when(promptUseCase.buscarPorIdAtivo("fd6ae793-f385-4d44-8b8f-b2571ebcf901")).thenReturn(
                Prompt.builder()
                        .id("fd6ae793-f385-4d44-8b8f-b2571ebcf901")
                        .modelIa("gpt-3.5")
                        .conteudo("system")
                        .build()
        );

        OpenIaResponseDto responseIa = mock(OpenIaResponseDto.class);
        OpenIaResponseDto.Choice choice = mock(OpenIaResponseDto.Choice.class);
        OpenIaResponseDto.Message message = mock(OpenIaResponseDto.Message.class);
        LinkedList<OpenIaResponseDto.Choice> choices = new LinkedList<>();
        choices.add(choice);

        when(responseIa.getChoices()).thenReturn(choices);
        when(choice.getMessage()).thenReturn(message);
        when(message.getContent()).thenReturn("isto não é JSON válido");
        when(gateway.enviarMensagem(any())).thenReturn(responseIa);

        assertThrows(
                ErroEnviarParaIaException.class,
                () -> iaUseCase.gerarOrcamento("texto qualquer")
        );
    }
}