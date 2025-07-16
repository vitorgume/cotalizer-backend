package com.gumeinteligenciacomercial.orcaja.application.gateway;

import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.PaymentResponseDto;
import com.mercadopago.client.payment.PaymentCreateRequest;

public interface PagamentoGateway {
    PaymentResponseDto enviarPagamento(PaymentCreateRequest paymentCreateRequest);
}
