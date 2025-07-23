package com.gumeinteligenciacomercial.orcaja.application.usecase;

import com.gumeinteligenciacomercial.orcaja.application.gateway.AssinaturaGateway;
import com.gumeinteligenciacomercial.orcaja.application.usecase.dto.AssinaturaDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AssinaturaUseCase {

    private final AssinaturaGateway assinaturaGateway;

    public void criarAssinatura(AssinaturaDto novaAssinatura) {
        log.info("Enviando nova assinatura para api de assinaturas. Assinatura: {}", novaAssinatura);

        assinaturaGateway.enviarNovaAssinatura(novaAssinatura);

        log.info("Nova assinatura enviada com sucesso.");
    }

    public void cancelar(String idUsuario) {
        log.info("Enviando cancelamento para a api de assinaturas. Id usuario: {}", idUsuario);

        assinaturaGateway.enviarCancelamento(idUsuario);

        log.info("Cancelamento enviado com sucesso.");
    }
}
