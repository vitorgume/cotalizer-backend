package com.gumeinteligenciacomercial.orcaja.application.usecase;

import com.gumeinteligenciacomercial.orcaja.domain.Avaliacao;
import com.gumeinteligenciacomercial.orcaja.domain.Usuario;
import jakarta.validation.constraints.Email;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
class AvaliacaoUseCaseTest {

    @Mock
    private EmailUseCase emailUseCase;

    @Mock
    private UsuarioUseCase usuarioUseCase;

    @InjectMocks
    private AvaliacaoUseCase avaliacaoUseCase;

    @Captor
    private ArgumentCaptor<Usuario> usuarioCaptor;

    private Avaliacao avaliacao;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        avaliacao = Avaliacao.builder()
                .idUsuario("id-teste 2")
                .nota(3)
                .sugestaoMelhoria("melhoria teste 2")
                .motivoNota("motivo nota teste 2")
                .build();

        usuario = Usuario.builder().id("id-teste 2").nome("Nome teste").build();
    }

    @Test
    void deveEnviarComSucesso() {
        Mockito.when(usuarioUseCase.consultarPorId(anyString())).thenReturn(usuario);
        Mockito.doNothing().when(emailUseCase).enviarAvaliacao(any(), any());
        Mockito.when(usuarioUseCase.alterar(anyString(), usuarioCaptor.capture())).thenReturn(usuario);

        avaliacaoUseCase.enviar(avaliacao);

        Mockito.verify(usuarioUseCase).consultarPorId(anyString());
        Mockito.verify(emailUseCase).enviarAvaliacao(any(), any());
        Mockito.verify(usuarioUseCase).alterar(anyString(), any());

        Assertions.assertTrue(usuarioCaptor.getValue().getFeedback());
    }
}