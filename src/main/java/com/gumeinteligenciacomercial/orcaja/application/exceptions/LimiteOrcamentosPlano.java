package com.gumeinteligenciacomercial.orcaja.application.exceptions;

public class LimiteOrcamentosPlano extends RuntimeException {
    public LimiteOrcamentosPlano() {
        super("Limite de orçamento atingindo para o plano do usuário.");
    }
}
