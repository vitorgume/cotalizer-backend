package com.gumeinteligenciacomercial.orcaja.infrastructure.dataprovider;

import com.gumeinteligenciacomercial.orcaja.application.gateway.AssinaturaGateway;
import com.gumeinteligenciacomercial.orcaja.application.usecase.dto.AssinaturaDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Component
public class AssinaturaDataProvider implements AssinaturaGateway {

    private final WebClient webClient;

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
        webClient.post()
                .uri("http://localhost:8081/assinaturas")
                .header("x-api-key", API_ASSINATURAS_KEY)
                .bodyValue(novaAssinatura)
                .retrieve()
                .bodyToMono(AssinaturaDto.class)
                .block();
    }

    @Override
    public void enviarCancelamento(String idUsuario) {
        webClient.delete()
                .uri("http://localhost:8081/assinaturas/" + idUsuario)
                .header("x-api-key", API_ASSINATURAS_KEY)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

    }
}
