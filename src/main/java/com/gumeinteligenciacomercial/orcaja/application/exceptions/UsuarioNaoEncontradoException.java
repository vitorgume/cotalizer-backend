package com.gumeinteligenciacomercial.orcaja.application.exceptions;

public class UsuarioNaoEncontradoException extends RuntimeException {
    public UsuarioNaoEncontradoException() {
        super("Usuário não encontrado pelo seu id.");
    }
}
