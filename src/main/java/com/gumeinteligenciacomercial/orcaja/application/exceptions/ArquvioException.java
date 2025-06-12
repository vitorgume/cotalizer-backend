package com.gumeinteligenciacomercial.orcaja.application.exceptions;

public class ArquvioException extends RuntimeException {
    public ArquvioException(String message, Exception e) {
        super(message, e);
    }
}
