package com.gumeinteligenciacomercial.orcaja.application.usecase;

import com.gumeinteligenciacomercial.orcaja.application.gateway.AssinaturaGateway;
import com.gumeinteligenciacomercial.orcaja.application.usecase.dto.AssinaturaDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class AssinaturaUseCaseTest {

    @Mock
    private AssinaturaGateway assinaturaGateway;

    @InjectMocks
    private AssinaturaUseCase assinaturaUseCase;

    @Test
    void criarAssinaturaDeveEnviarNovaAssinaturaAoGateway() {
        AssinaturaDto dto = AssinaturaDto.builder()
                .paymentMethodId("CARTAO")
                .customerEmail("EMAILTESTE")
                .idUsuario("user-123")
                .build();

        assinaturaUseCase.criarAssinatura(dto);

        verify(assinaturaGateway, times(1)).enviarNovaAssinatura(dto);
        verifyNoMoreInteractions(assinaturaGateway);
    }

    @Test
    void cancelarDeveEnviarCancelamentoAoGateway() {
        String usuarioId = "user-123";

        assinaturaUseCase.cancelar(usuarioId);

        verify(assinaturaGateway, times(1)).enviarCancelamento(usuarioId);
        verifyNoMoreInteractions(assinaturaGateway);
    }
}