package com.gumeinteligenciacomercial.orcaja.application.usecase;

import com.gumeinteligenciacomercial.orcaja.application.gateway.PagamentoGateway;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.CardPaymentDto;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.PaymentResponseDto;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.common.IdentificationRequest;
import com.mercadopago.client.payment.PaymentCreateRequest;
import com.mercadopago.client.payment.PaymentPayerRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PagamentoUseCase {

    private final PagamentoGateway gateway;

    @Value("${mp.acess.token}")
    private final String ACESS_TOKEN_MP;

    public PaymentResponseDto pagar(CardPaymentDto cardPaymentDTO) {

        MercadoPagoConfig.setAccessToken(ACESS_TOKEN_MP);

        PaymentCreateRequest paymentCreateRequest =
                PaymentCreateRequest.builder()
                        .transactionAmount(cardPaymentDTO.getTransactionAmount())
                        .token(cardPaymentDTO.getToken())
                        .description(cardPaymentDTO.getProductDescription())
                        .installments(cardPaymentDTO.getInstallments())
                        .paymentMethodId(cardPaymentDTO.getPaymentMethodId())
                        .payer(
                                PaymentPayerRequest.builder()
                                        .email(cardPaymentDTO.getPayer().getEmail())
                                        .identification(
                                                IdentificationRequest.builder()
                                                        .type(cardPaymentDTO.getPayer().getIdentification().getType())
                                                        .number(cardPaymentDTO.getPayer().getIdentification().getNumber())
                                                        .build())
                                        .build())
                        .build();

        return gateway.enviarPagamento(paymentCreateRequest);
    }
}
