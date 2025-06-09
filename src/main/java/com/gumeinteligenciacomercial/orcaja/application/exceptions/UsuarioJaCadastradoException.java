package com.gumeinteligenciacomercial.orcaja.application.exceptions;

public class UsuarioJaCadastradoException extends RuntimeException {
    public UsuarioJaCadastradoException() {
        super("Usuário já cadastrado com esse cpf.");
    }
}
