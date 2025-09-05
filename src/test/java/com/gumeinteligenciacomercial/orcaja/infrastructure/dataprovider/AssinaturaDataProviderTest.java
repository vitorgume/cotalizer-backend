package com.gumeinteligenciacomercial.orcaja.infrastructure.dataprovider;

import com.gumeinteligenciacomercial.orcaja.application.usecase.dto.AssinaturaDto;
import com.gumeinteligenciacomercial.orcaja.infrastructure.exceptions.DataProviderException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class AssinaturaDataProviderTest {

    private static final String API_KEY = "minha-api-key";
    private static final String URL_BASE = "http://localhost:8081/assinaturas/";
    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestBodyUriSpec postUriSpec;

    @Mock
    private WebClient.RequestBodySpec postBodySpec;

    @Mock
    private WebClient.RequestHeadersSpec postHeadersSpec;

    @Mock
    private WebClient.ResponseSpec postResponseSpec;

    @Mock
    private Mono<AssinaturaDto> postMono;

    @Mock
    private WebClient.RequestHeadersUriSpec deleteUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec deleteHeadersSpec;

    @Mock
    private WebClient.ResponseSpec deleteResponseSpec;

    @Mock
    private Mono<Map> deleteMono;

    private AssinaturaDataProvider provider;

    @BeforeEach
    void setUp() {
        provider = new AssinaturaDataProvider(webClient, API_KEY, URL_BASE);
    }

    @Test
    void enviarNovaAssinaturaDeveChamarWebClientCorretamente() {
        AssinaturaDto dto = AssinaturaDto.builder().build();

        given(postMono.block()).willReturn(dto);
        given(webClient.post()).willReturn(postUriSpec);
        given(postUriSpec.uri(URL_BASE)).willReturn(postBodySpec);
        given(postBodySpec.header("x-api-key", API_KEY)).willReturn(postBodySpec);
        given(postBodySpec.bodyValue(any(AssinaturaDto.class))).willReturn(postHeadersSpec);
        given(postHeadersSpec.retrieve()).willReturn(postResponseSpec);
        given(postResponseSpec.bodyToMono(AssinaturaDto.class)).willReturn(postMono);

        assertDoesNotThrow(() -> provider.enviarNovaAssinatura(dto));

        then(webClient).should().post();
        then(postUriSpec).should().uri(URL_BASE);
        then(postBodySpec).should().header("x-api-key", API_KEY);
        then(postBodySpec).should().bodyValue(dto);
        then(postHeadersSpec).should().retrieve();
        then(postResponseSpec).should().bodyToMono(AssinaturaDto.class);
        then(postMono).should().block();
    }

    @Test
    void enviarNovaAssinaturaQuandoFalharDeveLancarDataProviderException() {
        AssinaturaDto dto = AssinaturaDto.builder().build();

        given(webClient.post()).willReturn(postUriSpec);
        given(postUriSpec.uri(URL_BASE)).willReturn(postBodySpec);
        given(postBodySpec.header("x-api-key", API_KEY)).willReturn(postBodySpec);
        given(postBodySpec.bodyValue(any(AssinaturaDto.class))).willReturn(postHeadersSpec);
        given(postHeadersSpec.retrieve()).willReturn(postResponseSpec);
        given(postResponseSpec.bodyToMono(AssinaturaDto.class)).willReturn(postMono);

        given(postMono.block()).willThrow(new RuntimeException("erro remoto"));

        DataProviderException ex = assertThrows(
                DataProviderException.class,
                () -> provider.enviarNovaAssinatura(dto),
                "esperava DataProviderException quando o block() falha"
        );
        assertEquals(
                "Erro ao enviar nova assinatura para a API de assinaturas.",
                ex.getMessage()
        );

        then(postMono).should().block();
    }

    @Test
    void enviarCancelamentoDeveChamarWebClientCorretamente() {
        String userId = "usuario-123";

        given(deleteMono.block()).willReturn(Map.of());
        given(webClient.delete()).willReturn(deleteUriSpec);
        given(deleteUriSpec.uri(startsWith(URL_BASE))).willReturn(deleteHeadersSpec);
        given(deleteHeadersSpec.header("x-api-key", API_KEY)).willReturn(deleteHeadersSpec);
        given(deleteHeadersSpec.retrieve()).willReturn(deleteResponseSpec);
        given(deleteResponseSpec.bodyToMono(Map.class)).willReturn(deleteMono);

        assertDoesNotThrow(() -> provider.enviarCancelamento(userId));

        then(webClient).should().delete();
        then(deleteUriSpec).should().uri(URL_BASE + userId);
        then(deleteHeadersSpec).should().header("x-api-key", API_KEY);
        then(deleteHeadersSpec).should().retrieve();
        then(deleteResponseSpec).should().bodyToMono(Map.class);
        then(deleteMono).should().block();
    }

    @Test
    void enviarCancelamentoQuandoFalharDeveLancarDataProviderException() {
        String userId = "usuario-456";
        given(deleteMono.block()).willThrow(new RuntimeException("erro cancel"));
        given(webClient.delete()).willReturn(deleteUriSpec);
        given(deleteUriSpec.uri(startsWith(URL_BASE + "/"))).willReturn(deleteHeadersSpec);
        given(deleteHeadersSpec.header("x-api-key", API_KEY)).willReturn(deleteHeadersSpec);
        given(deleteHeadersSpec.retrieve()).willReturn(deleteResponseSpec);
        given(deleteResponseSpec.bodyToMono(Map.class)).willReturn(deleteMono);

        DataProviderException ex = assertThrows(
                DataProviderException.class,
                () -> provider.enviarCancelamento(userId)
        );
        assertEquals(
                "Erro ao enviar cancelamento de assinatura para a API de assinaturas.",
                ex.getMessage()
        );
    }
}