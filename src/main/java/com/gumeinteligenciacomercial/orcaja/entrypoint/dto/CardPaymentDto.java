package com.gumeinteligenciacomercial.orcaja.entrypoint.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
@Setter
public class CardPaymentDto {
    private String token;

    private String issuerId;

    private String paymentMethodId;

    private BigDecimal transactionAmount;

    private Integer installments;

    private String productDescription;

    private PayerDto payer;
}
