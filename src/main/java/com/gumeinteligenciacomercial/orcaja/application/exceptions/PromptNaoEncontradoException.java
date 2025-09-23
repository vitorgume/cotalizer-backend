package com.gumeinteligenciacomercial.orcaja.application.exceptions;

public class PromptNaoEncontradoException extends RuntimeException {

    public PromptNaoEncontradoException() {
        super("Prompt não encontrado.");
    }
}
