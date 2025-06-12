package com.gumeinteligenciacomercial.orcaja.infrastructure.dataprovider;

import com.gumeinteligenciacomercial.orcaja.application.gateway.LoginGateway;
import com.gumeinteligenciacomercial.orcaja.infrastructure.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LoginDataProvider implements LoginGateway {

    private final JwtUtil jwtUtil;

    @Override
    public String generateToken(String email) {
        return jwtUtil.generateToken(email);
    }
}
