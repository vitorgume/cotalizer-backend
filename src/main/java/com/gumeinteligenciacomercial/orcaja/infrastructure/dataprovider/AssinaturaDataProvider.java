package com.gumeinteligenciacomercial.orcaja.infrastructure.dataprovider;

import com.gumeinteligenciacomercial.orcaja.application.gateway.PagamentoGateway;
import com.gumeinteligenciacomercial.orcaja.domain.Assinatura;
import com.gumeinteligenciacomercial.orcaja.infrastructure.exceptions.DataProviderException;
import com.mercadopago.resources.preapproval.Preapproval;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class AssinaturaDataProvider implements PagamentoGateway {

    @Value("${mp.acess.token}")
    private final String ACESS_TOKEN_MP;

    @Value("${mp.plano.id}")
    private final String PLANO_ID;

    private final WebClient webClient;

    public AssinaturaDataProvider(
            @Value("${mp.acess.token}") String ACESS_TOKEN_MP,
            @Value("${mp.plano.id}") String PLANO_ID,
            WebClient webClient
    ) {
        this.ACESS_TOKEN_MP = ACESS_TOKEN_MP;
        this.PLANO_ID = PLANO_ID;
        this.webClient = webClient;
    }

    @Override
    public Assinatura criarAssinatura(Assinatura novaAssinatura) {

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("preapproval_plan_id", PLANO_ID);
        requestBody.put("card_token_id", novaAssinatura.getCardTokenId());
        requestBody.put("payer_email", novaAssinatura.getEmail());
        requestBody.put("payment_method_id", novaAssinatura.getPaymentMethodId());
        requestBody.put("back_url", "https://seusite.com/sucesso");
        Preapproval preapproval;

        try {
            preapproval = webClient
                    .post()
                    .uri("https://api.mercadopago.com/preapproval")
                    .header("Authorization", "Bearer " + ACESS_TOKEN_MP)
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Preapproval.class)
                    .block();
        } catch (Exception ex) {
            log.error("Erro ao criar assinatura", ex);
            throw new DataProviderException("Erro ao criar assinatura", ex.getCause());
        }



        return Assinatura.builder()
                .id(preapproval.getId())
                .status(preapproval.getStatus())
                .build();
    }
}
