package com.gumeinteligenciacomercial.orcaja.infrastructure.exceptions;

public class DataProviderException extends RuntimeException {
    public DataProviderException(String mensagem, Throwable cause) {
        super(mensagem, cause);
    }
}
