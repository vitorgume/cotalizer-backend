package com.gumeinteligenciacomercial.orcaja.infrastructure.dataprovider;

import com.gumeinteligenciacomercial.orcaja.application.gateway.AssinaturaGateway;
import com.gumeinteligenciacomercial.orcaja.application.usecase.dto.AssinaturaDto;
import com.gumeinteligenciacomercial.orcaja.infrastructure.exceptions.DataProviderException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Component
@Slf4j
public class AssinaturaDataProvider implements AssinaturaGateway {

    private final WebClient webClient;
    private final String MENSAGEM_ERRO_ENVIAR_NOVA_ASSINATURA_API_ASSINATURAS = "Erro ao enviar nova assinatura para a API de assinaturas.";
    private final String MENSAGEM_ERRO_ENVIAR_CANCELAMENTO_ASSINATURA_API = "Erro ao enviar cancelamento de assinatura para a API de assinaturas.";

    @Value("${security.api.key}")
    private final String API_ASSINATURAS_KEY;

    public AssinaturaDataProvider(
            WebClient webClient,
            @Value("${security.api.key}") String API_ASSINATURAS_KEY
    ) {
        this.webClient = webClient;
        this.API_ASSINATURAS_KEY = API_ASSINATURAS_KEY;
    }

    @Override
    public void enviarNovaAssinatura(AssinaturaDto novaAssinatura) {
        try {
            webClient.post()
                    .uri("http://localhost:8081/assinaturas")
                    .header("x-api-key", API_ASSINATURAS_KEY)
                    .bodyValue(novaAssinatura)
                    .retrieve()
                    .bodyToMono(AssinaturaDto.class)
                    .block();
        } catch (Exception ex) {
            log.error(MENSAGEM_ERRO_ENVIAR_NOVA_ASSINATURA_API_ASSINATURAS, ex);
            throw new DataProviderException(MENSAGEM_ERRO_ENVIAR_NOVA_ASSINATURA_API_ASSINATURAS, ex.getCause());
        }

    }

    @Override
    public void enviarCancelamento(String idUsuario) {
        try {
            webClient.delete()
                    .uri("http://localhost:8081/assinaturas/" + idUsuario)
                    .header("x-api-key", API_ASSINATURAS_KEY)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
        } catch (Exception ex) {
            log.error(MENSAGEM_ERRO_ENVIAR_CANCELAMENTO_ASSINATURA_API, ex);
            throw new DataProviderException(MENSAGEM_ERRO_ENVIAR_CANCELAMENTO_ASSINATURA_API, ex.getCause());
        }

    }
}
