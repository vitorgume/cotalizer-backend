package com.gumeinteligenciacomercial.orcaja.application.exceptions;

public class OrcamentoTradicionalNaoEncontradoException extends RuntimeException {

    public OrcamentoTradicionalNaoEncontradoException() {
        super("Orçamento tradicional não encontrado.");
    }
}
