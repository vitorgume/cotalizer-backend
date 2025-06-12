package com.gumeinteligenciacomercial.orcaja.application.usecase.ia.dto;

import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class MessagePromptIaDto {
    private String role;
    private String content;
}
