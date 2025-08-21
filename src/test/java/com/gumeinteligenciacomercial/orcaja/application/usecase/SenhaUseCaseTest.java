package com.gumeinteligenciacomercial.orcaja.application.usecase;

import com.gumeinteligenciacomercial.orcaja.application.exceptions.UsuarioNaoEncontradoException;
import com.gumeinteligenciacomercial.orcaja.domain.Usuario;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SenhaUseCaseTest {

    @Mock
    private EmailUseCase emailUseCase;

    @Mock
    private UsuarioUseCase usuarioUseCase;

    @Mock
    private CodigoAlteracaoSenhaUseCase codigoAlteracaoSenhaUseCase;

    @InjectMocks
    private SenhaUseCase senhaUseCase;

    @Captor
    private ArgumentCaptor<String> emailCaptor;

    @Captor
    private ArgumentCaptor<String> codigoCaptor1;

    @Captor
    private ArgumentCaptor<String> userIdCaptor;

    @Captor
    private ArgumentCaptor<String> codigoCaptor2;

    @Test
    void solicitarNovaSenhaSucessoDeveEnviarEmailEAdicionarCache() {
        String email = "user@example.com";
        String userId = "u123";
        Usuario usuario = Usuario.builder()
                .id(userId)
                .email(email)
                .build();
        when(usuarioUseCase.consultarPorEmail(email)).thenReturn(usuario);

        senhaUseCase.solicitarNovaSenha(email);

        verify(usuarioUseCase, times(1)).consultarPorEmail(email);
        verify(emailUseCase, times(1))
                .enviarAlteracaoDeSenha(emailCaptor.capture(), codigoCaptor1.capture());
        verify(codigoAlteracaoSenhaUseCase, times(1))
                .adicionarAoCache(userIdCaptor.capture(), codigoCaptor2.capture());

        assertEquals(email, emailCaptor.getValue());
        assertEquals(userId, userIdCaptor.getValue());
        String code1 = codigoCaptor1.getValue();
        String code2 = codigoCaptor2.getValue();
        assertNotNull(code1, "O código gerado não deve ser nulo");
        assertNotNull(code2, "O código gerado não deve ser nulo");
        assertEquals(code1, code2, "O mesmo código deve ser usado no email e no cache");

        verifyNoMoreInteractions(emailUseCase, codigoAlteracaoSenhaUseCase);
    }

    @Test
    void solicitarNovaSenhaUsuarioNaoEncontradoDeveLancarException() {
        String email = "user@example.com";
        when(usuarioUseCase.consultarPorEmail(email))
                .thenThrow(new UsuarioNaoEncontradoException());

        assertThrows(UsuarioNaoEncontradoException.class,
                () -> senhaUseCase.solicitarNovaSenha(email)
        );

        verify(usuarioUseCase, times(1)).consultarPorEmail(email);
        verifyNoInteractions(emailUseCase, codigoAlteracaoSenhaUseCase);
    }
}