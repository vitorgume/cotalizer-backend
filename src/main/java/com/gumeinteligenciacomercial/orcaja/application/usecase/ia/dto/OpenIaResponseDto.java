package com.gumeinteligenciacomercial.orcaja.application.usecase.ia.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class OpenIaResponseDto {
    private String id;
    private String object;
    private Long created;
    private String model;
    private List<Choice> choices;
    private Usage usage;

    @AllArgsConstructor
    @Getter
    @Setter
    @Builder
    public static class Choice {
        private int index;
        private Message message;
        private String finish_reason;
    }

    @AllArgsConstructor
    @Getter
    @Setter
    @Builder
    public static class Message {
        private String role;
        private String content;
    }

    @AllArgsConstructor
    @Getter
    @Setter
    @Builder
    public static class Usage {
        private int prompt_tokens;
        private int completion_tokens;
        private int total_tokens;
    }
}
