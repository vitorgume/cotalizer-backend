package com.gumeinteligenciacomercial.orcaja.infrastructure.dataprovider;

import com.gumeinteligenciacomercial.orcaja.application.gateway.IaGateway;
import com.gumeinteligenciacomercial.orcaja.application.usecase.ia.dto.OpenIaResponseDto;
import com.gumeinteligenciacomercial.orcaja.application.usecase.ia.dto.PromptDto;
import com.gumeinteligenciacomercial.orcaja.infrastructure.exceptions.DataProviderException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.time.Duration;

@Component
@Slf4j
public class IaDataProvider implements IaGateway {

    @Value("${openia.api.key}")
    private final String apiKey;

    private final WebClient webClient;

    private final String MENSAGEM_ERRO_ENVIAR_ORCAMENTO_IA = "Erro ao enviar orçamento para IA.";

    public IaDataProvider(
            @Value("${openia.api.key}") String apiKey,
            WebClient webClient
    ) {
        this.apiKey = apiKey;
        this.webClient = webClient;
    }

    @Override
    public OpenIaResponseDto enviarMensagem(PromptDto prompt) {

        return webClient
                .post()
                .uri("/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .bodyValue(prompt)
                .retrieve()
                .bodyToMono(OpenIaResponseDto.class)
                .retryWhen(
                        Retry.backoff(3, Duration.ofSeconds(2))
                                .filter(throwable -> {
                                    log.warn("Tentando enviar orçamento para IA novamente: {}", throwable.getMessage());
                                    return true;
                                })
                )
                .doOnError(e -> {
                    log.error("Erro ao enviar prompt após tentativas.", e);
                    throw new DataProviderException(MENSAGEM_ERRO_ENVIAR_ORCAMENTO_IA, e.getCause());
                })
                .block();
    }
}
