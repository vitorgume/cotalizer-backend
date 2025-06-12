package com.gumeinteligenciacomercial.orcaja.application.exceptions;

public class ConversaoJsonException extends RuntimeException {
    public ConversaoJsonException(Exception e) {
        super("Erro ao converter texto para json", e);
    }
}
