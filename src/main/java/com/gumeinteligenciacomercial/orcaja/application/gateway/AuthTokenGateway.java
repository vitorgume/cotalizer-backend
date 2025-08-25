package com.gumeinteligenciacomercial.orcaja.application.gateway;

import java.util.Collection;
import java.util.List;

public interface AuthTokenGateway {
    String generateAccessToken(String subjectEmail, String userId, Collection<String> roles);

    String generateRefreshToken(String subjectEmail, String userId);

    ParsedToken parse(String jwt);

    record ParsedToken(String subjectEmail, String userId, String type, List<String> roles) {}
}
