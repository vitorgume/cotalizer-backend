package com.gumeinteligenciacomercial.orcaja.application.usecase;

import com.gumeinteligenciacomercial.orcaja.application.exceptions.CredenciasIncorretasException;
import com.gumeinteligenciacomercial.orcaja.application.gateway.AuthTokenGateway;
import com.gumeinteligenciacomercial.orcaja.application.gateway.LoginGateway;
import com.gumeinteligenciacomercial.orcaja.domain.AuthResult;
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
    private AuthTokenGateway tokenGateway;

    @Mock
    private CriptografiaUseCase criptografiaUseCase;

    @InjectMocks
    private LoginUseCase loginUseCase;

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
        verifyNoMoreInteractions(usuarioUseCase, criptografiaUseCase, tokenGateway);
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
        verifyNoMoreInteractions(usuarioUseCase, criptografiaUseCase, tokenGateway);
    }

    @Test
    void autenticarSucesso_deveGerarTokensERetornarAuthResult() {
        String email = "ok@example.com";
        String senhaRaw = "secret";
        String hashed = "hashed";
        String userId = "u-123";

        Login loginInput = Login.builder()
                .email(email)
                .senha(senhaRaw)
                .build();
        Usuario usuario = Usuario.builder()
                .id(userId)
                .email(email)
                .senha(hashed)
                .build();

        when(usuarioUseCase.consultarPorEmail(email)).thenReturn(usuario);
        when(criptografiaUseCase.validaSenha(senhaRaw, hashed)).thenReturn(true);
        when(tokenGateway.generateAccessToken(email, userId, null)).thenReturn("ACCESS_X");
        when(tokenGateway.generateRefreshToken(email, userId)).thenReturn("REFRESH_Y");

        AuthResult res = loginUseCase.autenticar(loginInput);

        assertNotNull(res);
        assertEquals(usuario, res.getUsuario());
        assertEquals("ACCESS_X", res.getAccessToken());
        assertEquals("REFRESH_Y", res.getRefreshToken());

        verify(usuarioUseCase).consultarPorEmail(email);
        verify(criptografiaUseCase).validaSenha(senhaRaw, hashed);
        verify(tokenGateway).generateAccessToken(email, userId, null);
        verify(tokenGateway).generateRefreshToken(email, userId);
        verifyNoMoreInteractions(usuarioUseCase, criptografiaUseCase, tokenGateway);
    }

    @Test
    void autenticarQuandoUsuarioNulo_deveLancarCredenciasIncorretas() {
        String email = "missing@example.com";
        String senha = "x";

        Login loginInput = Login.builder()
                .email(email)
                .senha(senha)
                .build();

        when(usuarioUseCase.consultarPorEmail(email)).thenReturn(null);

        assertThrows(CredenciasIncorretasException.class,
                () -> loginUseCase.autenticar(loginInput));

        verify(usuarioUseCase).consultarPorEmail(email);
        verifyNoInteractions(criptografiaUseCase, tokenGateway);
    }
}