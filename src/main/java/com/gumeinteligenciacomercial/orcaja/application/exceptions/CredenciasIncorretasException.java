package com.gumeinteligenciacomercial.orcaja.application.exceptions;

    public class CredenciasIncorretasException extends RuntimeException {
        public CredenciasIncorretasException() {
            super("Credências incorretas.");
        }
    }
