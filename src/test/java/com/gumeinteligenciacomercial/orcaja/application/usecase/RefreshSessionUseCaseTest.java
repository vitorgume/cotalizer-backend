package com.gumeinteligenciacomercial.orcaja.application.usecase;

import com.gumeinteligenciacomercial.orcaja.application.gateway.AuthTokenGateway;
import com.gumeinteligenciacomercial.orcaja.domain.RefreshResult;
import com.gumeinteligenciacomercial.orcaja.domain.Usuario;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshSessionUseCaseTest {

    @Mock
    private UsuarioUseCase usuarioUseCase;

    @Mock
    private AuthTokenGateway tokenGateway;

    @InjectMocks
    private RefreshSessionUseCase sut;

    @Captor
    ArgumentCaptor<Collection<String>> rolesCaptor;

    private Usuario mockUsuario(String id, String email) {
        Usuario u = mock(Usuario.class);
        when(u.getId()).thenReturn(id);
        when(u.getEmail()).thenReturn(email);
        return u;
    }

    @Test
    void deveRenovarTokensQuandoRefreshValido() {
        String refreshToken = "refresh.jwt";
        String email = "alice@example.com";
        String userId = "user-123";

        var parsed = new AuthTokenGateway.ParsedToken(email, userId, "refresh", List.of("USER"));
        when(tokenGateway.parse(refreshToken)).thenReturn(parsed);

        Usuario usuario = mockUsuario(userId, email);
        when(usuarioUseCase.consultarPorEmail(email)).thenReturn(usuario);

        when(tokenGateway.generateAccessToken(email, userId, null)).thenReturn("NEW_ACCESS_123");
        when(tokenGateway.generateRefreshToken(email, userId)).thenReturn("NEW_REFRESH_456");

        RefreshResult out = sut.renovar(refreshToken);

        assertNotNull(out);
        assertEquals("NEW_ACCESS_123", out.getNewAccessToken());
        assertEquals("NEW_REFRESH_456", out.getNewRefreshToken());

        InOrder inOrder = inOrder(tokenGateway, usuarioUseCase);
        inOrder.verify(tokenGateway).parse(refreshToken);
        inOrder.verify(usuarioUseCase).consultarPorEmail(email);
        inOrder.verify(tokenGateway).generateAccessToken(eq(email), eq(userId), any());
        inOrder.verify(tokenGateway).generateRefreshToken(email, userId);

        verify(tokenGateway).generateAccessToken(eq(email), eq(userId), rolesCaptor.capture());
        assertNull(rolesCaptor.getValue());

        verifyNoMoreInteractions(tokenGateway, usuarioUseCase);
    }

    @Test
    void deveLancarIllegalArgumentQuandoTokenNaoEhRefresh() {
        String anyToken = "access.jwt";

        when(tokenGateway.parse(anyToken))
                .thenReturn(new AuthTokenGateway.ParsedToken("bob@example.com", "id-1", "access", List.of("USER")));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> sut.renovar(anyToken));
        assertEquals("Token não é refresh", ex.getMessage());

        verify(tokenGateway).parse(anyToken);
        verifyNoInteractions(usuarioUseCase);
        verify(tokenGateway, never()).generateAccessToken(any(), any(), any());
        verify(tokenGateway, never()).generateRefreshToken(any(), any());
    }

    @Test
    void deveLancarIllegalArgumentQuandoTipoDoTokenEhNull() {
        String anyToken = "weird.jwt";
        when(tokenGateway.parse(anyToken))
                .thenReturn(new AuthTokenGateway.ParsedToken("x@example.com", "id-x", null, null));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> sut.renovar(anyToken));
        assertEquals("Token não é refresh", ex.getMessage());

        verify(tokenGateway).parse(anyToken);
        verifyNoInteractions(usuarioUseCase);
    }

    @Test
    void devePropagarExcecaoQuandoParseFalha() {
        String token = "broken.jwt";
        when(tokenGateway.parse(token)).thenThrow(new RuntimeException("token inválido"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> sut.renovar(token));
        assertEquals("token inválido", ex.getMessage());

        verify(tokenGateway).parse(token);
        verifyNoInteractions(usuarioUseCase);
        verify(tokenGateway, never()).generateAccessToken(any(), any(), any());
        verify(tokenGateway, never()).generateRefreshToken(any(), any());
    }

    @Test
    void devePropagarExcecaoQuandoUsuarioNaoEncontrado() {

        String token = "refresh.ok";
        String email = "notfound@example.com";

        when(tokenGateway.parse(token))
                .thenReturn(new AuthTokenGateway.ParsedToken(email, "id-nao-importa", "refresh", null));
        when(usuarioUseCase.consultarPorEmail(email))
                .thenThrow(new NoSuchElementException("não encontrado"));

        NoSuchElementException ex = assertThrows(NoSuchElementException.class, () -> sut.renovar(token));
        assertEquals("não encontrado", ex.getMessage());

        verify(tokenGateway).parse(token);
        verify(usuarioUseCase).consultarPorEmail(email);
        verify(tokenGateway, never()).generateAccessToken(any(), any(), any());
        verify(tokenGateway, never()).generateRefreshToken(any(), any());
    }

    @Test
    void deveLancarNullPointerQuandoUsuarioRetornaNull() {
        String token = "refresh.ok";
        String email = "ghost@example.com";

        when(tokenGateway.parse(token))
                .thenReturn(new AuthTokenGateway.ParsedToken(email, "id-ghost", "refresh", null));
        when(usuarioUseCase.consultarPorEmail(email)).thenReturn(null); // defensivo

        assertThrows(NullPointerException.class, () -> sut.renovar(token));

        verify(tokenGateway).parse(token);
        verify(usuarioUseCase).consultarPorEmail(email);
        verify(tokenGateway, never()).generateAccessToken(any(), any(), any());
        verify(tokenGateway, never()).generateRefreshToken(any(), any());
    }

    @Test
    void devePropagarExcecaoQuandoGerarAccessFalha() {
        String token = "refresh.ok";
        String email = "carol@example.com";
        String userId = "id-777";

        when(tokenGateway.parse(token))
                .thenReturn(new AuthTokenGateway.ParsedToken(email, userId, "refresh", List.of("USER")));

        Usuario usuario = mockUsuario(userId, email);
        when(usuarioUseCase.consultarPorEmail(email)).thenReturn(usuario);

        when(tokenGateway.generateAccessToken(email, userId, null))
                .thenThrow(new IllegalStateException("falha ao gerar access"));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> sut.renovar(token));
        assertEquals("falha ao gerar access", ex.getMessage());

        verify(tokenGateway).parse(token);
        verify(usuarioUseCase).consultarPorEmail(email);
        verify(tokenGateway).generateAccessToken(email, userId, null);
        verify(tokenGateway, never()).generateRefreshToken(any(), any());
    }

    @Test
    void devePropagarExcecaoQuandoGerarRefreshFalha() {
        String token = "refresh.ok";
        String email = "dave@example.com";
        String userId = "id-999";

        when(tokenGateway.parse(token))
                .thenReturn(new AuthTokenGateway.ParsedToken(email, userId, "refresh", List.of("USER")));

        Usuario usuario = mockUsuario(userId, email);
        when(usuarioUseCase.consultarPorEmail(email)).thenReturn(usuario);

        when(tokenGateway.generateAccessToken(email, userId, null)).thenReturn("ACCESS_OK");
        when(tokenGateway.generateRefreshToken(email, userId))
                .thenThrow(new IllegalStateException("falha ao gerar refresh"));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> sut.renovar(token));
        assertEquals("falha ao gerar refresh", ex.getMessage());

        verify(tokenGateway).parse(token);
        verify(usuarioUseCase).consultarPorEmail(email);
        verify(tokenGateway).generateAccessToken(email, userId, null);
        verify(tokenGateway).generateRefreshToken(email, userId);
    }

    @Test
    void deveNegarTipoRefreshCaseSensitive() {
        String token = "maybe-refresh";
        when(tokenGateway.parse(token))
                .thenReturn(new AuthTokenGateway.ParsedToken("eve@example.com", "id-eve", "REFRESH", List.of("USER")));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> sut.renovar(token));
        assertEquals("Token não é refresh", ex.getMessage());

        verify(tokenGateway).parse(token);
        verifyNoInteractions(usuarioUseCase);
    }
}