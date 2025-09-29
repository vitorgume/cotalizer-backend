package com.gumeinteligenciacomercial.orcaja.application.exceptions;

public class TemplateNaoEncontradoException extends RuntimeException {
    public TemplateNaoEncontradoException() {
        super("Template não encontrado.");
    }
}
