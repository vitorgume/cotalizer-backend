package com.gumeinteligenciacomercial.orcaja.application.exceptions;

public class CodigoInvalidoValidacaoEmailException extends RuntimeException {

    public CodigoInvalidoValidacaoEmailException() {
        super("Código para validação de email inválido.");
    }
}
