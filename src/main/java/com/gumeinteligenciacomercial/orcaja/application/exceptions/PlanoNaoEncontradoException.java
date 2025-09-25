package com.gumeinteligenciacomercial.orcaja.application.exceptions;

public class PlanoNaoEncontradoException extends RuntimeException {

    public PlanoNaoEncontradoException() {
        super("Plano não encontrado.");
    }
}
