package com.gumeinteligenciacomercial.orcaja.application.exceptions;

public class LimiteOrcamentosPlanoException extends RuntimeException {
    public LimiteOrcamentosPlanoException() {
        super("Limite de orçamento atingindo para o plano do usuário.");
    }
}
