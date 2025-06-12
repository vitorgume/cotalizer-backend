package com.gumeinteligenciacomercial.orcaja.application.usecase.ia.dto;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class PromptDto {
    private String model;
    private List<MessagePromptIaDto> messages;
}