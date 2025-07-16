package com.gumeinteligenciacomercial.orcaja.entrypoint.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class PagamentoRequest {
    private String token;
    private String issuer_id;
    private String payment_method_id;
    private Double transaction_amount;
    private Integer installments;
    private String description;
    private Payer payer;

    @AllArgsConstructor
    @Getter
    @Setter
    @Builder
    public static class Payer {
        private String email;
        private Identification identification;

        @AllArgsConstructor
        @Getter
        @Setter
        @Builder
        public static class Identification {
            private String type;
            private String number;
        }
    }
}
