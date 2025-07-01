package com.gumeinteligenciacomercial.orcaja.application.gateway;

import com.gumeinteligenciacomercial.orcaja.domain.Usuario;

public interface LoginGateway {
    String generateToken(String email, String id);
}
