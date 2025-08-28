package com.gumeinteligenciacomercial.orcaja.application.exceptions;

public class CodigoInvalidoAlteracaoSenha extends RuntimeException {
    public CodigoInvalidoAlteracaoSenha() {
        super("Código para alteração de senha inválido");
    }
}
