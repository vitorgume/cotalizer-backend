package com.gumeinteligenciacomercial.orcaja.application.exceptions;

public class ErroEnviarParaIaException extends RuntimeException {
    public ErroEnviarParaIaException() {
        super("Erro ao gerar orçamento com a IA");
    }
}
