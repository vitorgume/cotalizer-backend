package com.gumeinteligenciacomercial.orcaja.infrastructure.exceptions;

public class TokenInvalidoException extends RuntimeException {
    public TokenInvalidoException() {
        super("Token inválido.");
    }
}
