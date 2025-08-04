package com.gumeinteligenciacomercial.orcaja.infrastructure.dataprovider;

import com.gumeinteligenciacomercial.orcaja.application.usecase.ia.dto.OpenIaResponseDto;
import com.gumeinteligenciacomercial.orcaja.application.usecase.ia.dto.PromptDto;
import com.gumeinteligenciacomercial.orcaja.infrastructure.exceptions.DataProviderException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IaDataProviderTest {

    private static final String API_KEY = "teste-key";
    private static final String URI_PATH = "/chat/completions";

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.RequestBodySpec bodySpec;

    @Mock
    private WebClient.RequestHeadersSpec<?> headersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @Mock
    private PromptDto promptDto;

    @Mock
    private OpenIaResponseDto responseDto;

    private IaDataProvider provider;

    @BeforeEach
    void setUp() {
        // instância com a chave e o WebClient mockado
        provider = new IaDataProvider(API_KEY, webClient);

        // fluxograma normal de chamada
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(URI_PATH)).thenReturn(bodySpec);
        when(bodySpec.header("Authorization", "Bearer " + API_KEY))
                .thenReturn(bodySpec);
        doReturn(headersSpec)
                .when(bodySpec)
                .bodyValue(promptDto);

        when(headersSpec.retrieve()).thenReturn(responseSpec);
    }

    @Test
    void enviarMensagemDeveRetornarRespostaQuandoSucesso() {
        when(responseSpec.bodyToMono(OpenIaResponseDto.class))
                .thenReturn(Mono.just(responseDto));

        OpenIaResponseDto resultado = provider.enviarMensagem(promptDto);

        assertSame(responseDto, resultado);

        verify(webClient).post();
        verify(requestBodyUriSpec).uri(URI_PATH);
        verify(bodySpec).header("Authorization", "Bearer " + API_KEY);
        verify(bodySpec).bodyValue(promptDto);
        verify(headersSpec).retrieve();
        verify(responseSpec).bodyToMono(OpenIaResponseDto.class);
    }

    @Test
    void enviarMensagem_quandoRetrieveLancarErro_deveLancarDataProviderException() {
        when(headersSpec.retrieve()).thenThrow(new RuntimeException("erro remoto"));

        DataProviderException ex = assertThrows(
                DataProviderException.class,
                () -> provider.enviarMensagem(promptDto)
        );
        assertEquals("Erro ao enviar orçamento para IA.", ex.getMessage());

        verify(webClient).post();
        verify(requestBodyUriSpec).uri(URI_PATH);
        verify(bodySpec).header("Authorization", "Bearer " + API_KEY);
        verify(bodySpec).bodyValue(promptDto);
        verify(headersSpec).retrieve();

        verifyNoInteractions(responseSpec);
    }
}