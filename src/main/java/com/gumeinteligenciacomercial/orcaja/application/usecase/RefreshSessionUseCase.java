package com.gumeinteligenciacomercial.orcaja.application.usecase;

import com.gumeinteligenciacomercial.orcaja.application.gateway.AuthTokenGateway;
import com.gumeinteligenciacomercial.orcaja.domain.RefreshResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshSessionUseCase {

    private final UsuarioUseCase usuarioUseCase;
    private final AuthTokenGateway tokenGateway;

    public RefreshResult renovar(String refreshToken) {
        AuthTokenGateway.ParsedToken parsed = tokenGateway.parse(refreshToken);
        if (!"refresh".equals(parsed.type())) {
            throw new IllegalArgumentException("Token não é refresh");
        }

        var usuario = usuarioUseCase.consultarPorEmail(parsed.subjectEmail());

        String newAccess  = tokenGateway.generateAccessToken(usuario.getEmail(), usuario.getId(), null);
        String newRefresh = tokenGateway.generateRefreshToken(usuario.getEmail(), usuario.getId());

        return new RefreshResult(newAccess, newRefresh);
    }
}
