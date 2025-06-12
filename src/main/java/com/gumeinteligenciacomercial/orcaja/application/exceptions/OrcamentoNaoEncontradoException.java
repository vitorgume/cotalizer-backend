package com.gumeinteligenciacomercial.orcaja.application.exceptions;

public class OrcamentoNaoEncontradoException extends RuntimeException {
    public OrcamentoNaoEncontradoException() {
        super("Orçamento não encontrado pelo seu id.");
    }
}
