package com.gumeinteligenciacomercial.orcaja.entrypoint.mapper;

import com.gumeinteligenciacomercial.orcaja.domain.Assinatura;
import com.gumeinteligenciacomercial.orcaja.domain.PayerIdentification;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.AssinaturaRequestDto;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.AssinaturaResponseDto;

public class AssinaturaMapper {

    public static Assinatura paraDomain(AssinaturaRequestDto dto) {
        return Assinatura.builder()
                .cardTokenId(dto.getCardTokenId())
                .email(dto.getEmail())
                .paymentMethodId(dto.getPaymentMethodId())
                .idUsuario(dto.getIdUsuario())
                .build();
    }

    public static AssinaturaResponseDto paraDto(Assinatura assinatura) {
        return AssinaturaResponseDto.builder()
                .id(assinatura.getId())
                .status(assinatura.getStatus())
                .build();
    }
}
