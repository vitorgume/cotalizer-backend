package com.gumeinteligenciacomercial.orcaja.entrypoint.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class AssinaturaResponseDto {
    private String id;
    private String status;
}
