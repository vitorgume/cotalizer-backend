package com.gumeinteligenciacomercial.orcaja.infrastructure.dataprovider;

import com.gumeinteligenciacomercial.orcaja.application.gateway.PagamentoGateway;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.PaymentResponseDto;
import com.gumeinteligenciacomercial.orcaja.infrastructure.exceptions.DataProviderException;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.payment.PaymentCreateRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PagamentoDataProvider implements PagamentoGateway {

    @Override
    public PaymentResponseDto enviarPagamento(PaymentCreateRequest paymentCreateRequest) {

        PaymentClient paymentClient = new PaymentClient();

        try {
            Payment createdPayment = paymentClient.create(paymentCreateRequest);

            return new PaymentResponseDto(
                    createdPayment.getId(),
                    String.valueOf(createdPayment.getStatus()),
                    createdPayment.getStatusDetail());

        } catch (MPApiException apiException) {
            log.error(apiException.getApiResponse().getContent());
            throw new DataProviderException("Erro ao enviar pagamento", apiException.getCause());
        } catch (MPException exception) {
            log.error(exception.getMessage());
            throw new DataProviderException(exception.getMessage(), exception.getCause());
        }
    }
}
