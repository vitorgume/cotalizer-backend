package com.gumeinteligenciacomercial.orcaja.application.gateway;

public interface LoginGateway {
    String generateToken(String email);
}
