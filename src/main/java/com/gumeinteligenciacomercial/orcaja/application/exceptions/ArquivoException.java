package com.gumeinteligenciacomercial.orcaja.application.exceptions;

public class ArquivoException extends RuntimeException {
    public ArquivoException(String message, Exception e) {
        super(message, e);
    }
}
