package com.gumeinteligenciacomercial.orcaja.application.usecase;

import com.gumeinteligenciacomercial.orcaja.application.exceptions.CredenciasIncorretasException;
import com.gumeinteligenciacomercial.orcaja.application.gateway.LoginGateway;
import com.gumeinteligenciacomercial.orcaja.domain.Login;
import com.gumeinteligenciacomercial.orcaja.domain.Usuario;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginUseCaseTest {

    @Mock
    private UsuarioUseCase usuarioUseCase;

    @Mock
    private LoginGateway gateway;
    @Mock
    private CriptografiaUseCase criptografiaUseCase;

    @InjectMocks
    private LoginUseCase loginUseCase;

//    @Test
//    void autenticarSucessoRetornaLoginCorreto() {
//        String email = "john@example.com";
//        String senhaRaw = "password";
//        String hashed = "hashedPassword";
//        String usuarioId = "u1";
//        String token = "jwt-token";
//
//        Login loginInput = Login.builder()
//                .email(email)
//                .senha(senhaRaw)
//                .build();
//
//        Usuario usuario = Usuario.builder()
//                .email(email)
//                .senha(hashed)
//                .id(usuarioId)
//                .build();
//
//        when(usuarioUseCase.consultarPorEmail(email)).thenReturn(usuario);
//        when(criptografiaUseCase.validaSenha(senhaRaw, hashed)).thenReturn(true);
//        when(gateway.generateToken(email, usuarioId)).thenReturn(token);
//
//        Login result = loginUseCase.autenticar(loginInput);
//
//        assertEquals(token, result.getToken());
//        assertEquals(email, result.getEmail());
//        assertEquals(usuarioId, result.getUsuarioId());
//
//        verify(usuarioUseCase).consultarPorEmail(email);
//        verify(criptografiaUseCase).validaSenha(senhaRaw, hashed);
//        verify(gateway).generateToken(email, usuarioId);
//        verifyNoMoreInteractions(usuarioUseCase, criptografiaUseCase, gateway);
//    }

    @Test
    void autenticarSenhaIncorretaLancarExcecao() {
        String email = "john@example.com";
        String senhaRaw = "wrong";
        String hashed = "hashedPassword";

        Login loginInput = Login.builder()
                .email(email)
                .senha(senhaRaw)
                .build();
        Usuario usuario = Usuario.builder()
                .email(email)
                .senha(hashed)
                .id("u1")
                .build();

        when(usuarioUseCase.consultarPorEmail(email)).thenReturn(usuario);
        when(criptografiaUseCase.validaSenha(senhaRaw, hashed)).thenReturn(false);

        assertThrows(CredenciasIncorretasException.class, () ->
                loginUseCase.autenticar(loginInput)
        );

        verify(usuarioUseCase).consultarPorEmail(email);
        verify(criptografiaUseCase).validaSenha(senhaRaw, hashed);
        verifyNoMoreInteractions(usuarioUseCase, criptografiaUseCase, gateway);
    }

    @Test
    void autenticarEmailIncorretoLancarExcecao() {
        String emailInput = "john@example.com";
        String emailUsuario = "jane@example.com";
        String senhaRaw = "password";

        Login loginInput = Login.builder()
                .email(emailInput)
                .senha(senhaRaw)
                .build();
        Usuario usuario = Usuario.builder()
                .email(emailUsuario)
                .senha("ignored")
                .id("u1")
                .build();

        when(usuarioUseCase.consultarPorEmail(emailInput)).thenReturn(usuario);

        assertThrows(CredenciasIncorretasException.class, () ->
                loginUseCase.autenticar(loginInput)
        );

        verify(usuarioUseCase).consultarPorEmail(emailInput);
        verify(criptografiaUseCase, never()).validaSenha(any(), any());
        verifyNoMoreInteractions(usuarioUseCase, criptografiaUseCase, gateway);
    }

//    @Test
//    void gerarTokenJwtDeveRetornarToken() {
//        String email = "john@example.com";
//        String usuarioId = "u1";
//        String token = "jwt-token";
//
//        Usuario usuario = Usuario.builder()
//                .id(usuarioId)
//                .email(email)
//                .build();
//
//        when(usuarioUseCase.consultarPorEmail(email)).thenReturn(usuario);
//        when(gateway.generateToken(email, usuarioId)).thenReturn(token);
//
//        String result = loginUseCase.gerarTokenJwt(email);
//
//        assertEquals(token, result);
//        verify(usuarioUseCase).consultarPorEmail(email);
//        verify(gateway).generateToken(email, usuarioId);
//        verifyNoMoreInteractions(usuarioUseCase, gateway);
//    }
}