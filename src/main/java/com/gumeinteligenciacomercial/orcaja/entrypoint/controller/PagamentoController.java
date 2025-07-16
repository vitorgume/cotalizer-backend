package com.gumeinteligenciacomercial.orcaja.entrypoint.controller;

import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.PagamentoRequest;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/process_payment")
@CrossOrigin
public class PagamentoController {

    @PostMapping
    public Payment pagar(@RequestBody PagamentoRequest pagamento) throws MPException {
        Map<String, String> customHeaders = new HashMap<>();
        customHeaders.put("x-idempotency-key", "");

        MPRequestOptions requestOptions = MPRequestOptions.builder()
                .customHeaders(customHeaders)
                .build();

        MercadoPagoConfig.setAccessToken("YOUR_ACCESS_TOKEN");

        PaymentClient client = new PaymentClient();

        PaymentCreateRequest paymentCreateRequest =
                PaymentCreateRequest.builder()
                        .transactionAmount(request.getTransactionAmount())
                        .token(request.getToken())
                        .description(request.getDescription())
                        .installments(request.getInstallments())
                        .paymentMethodId(request.getPaymentMethodId())
                        .payer(
                                PaymentPayerRequest.builder()
                                        .email(request.getPayer().getEmail())
                                        .firstName(request.getPayer().getFirstName())
                                        .identification(
                                                IdentificationRequest.builder()
                                                        .type(request.getPayer().getIdentification().getType())
                                                        .number(request.getPayer().getIdentification().getNumber())
                                                        .build())
                                        .build())
                        .build();

        client.create(paymentCreateRequest, requestOptions);
    }
}
