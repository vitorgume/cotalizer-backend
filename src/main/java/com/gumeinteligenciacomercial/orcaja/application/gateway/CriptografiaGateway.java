package com.gumeinteligenciacomercial.orcaja.application.gateway;

public interface CriptografiaGateway {
    String criptografar(String senha);
    boolean validarSenha(String senha, String senhaRepresentante);
}
