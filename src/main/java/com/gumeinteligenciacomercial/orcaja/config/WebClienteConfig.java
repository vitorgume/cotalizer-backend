package com.gumeinteligenciacomercial.orcaja.config;

import org.springframework.context.annotation.Bean;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

public class WebClienteConfig {
    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        return builder
                .baseUrl("https://api.openai.com/v1")
                .clientConnector(new ReactorClientHttpConnector(HttpClient.create()
                        .responseTimeout(
                                Duration.ofSeconds(30))))
                .build();
    }
}
