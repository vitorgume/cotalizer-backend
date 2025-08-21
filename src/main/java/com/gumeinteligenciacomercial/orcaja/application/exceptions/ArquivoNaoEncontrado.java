package com.gumeinteligenciacomercial.orcaja.application.exceptions;

public class ArquivoNaoEncontrado extends RuntimeException {

    public ArquivoNaoEncontrado() {
        super("Arquivo não encontrado.");
    }
}
