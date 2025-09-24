package com.gumeinteligenciacomercial.orcaja.application.usecase.google;

import com.gumeinteligenciacomercial.orcaja.application.exceptions.UsuarioNaoEncontradoException;
import com.gumeinteligenciacomercial.orcaja.application.usecase.UsuarioUseCase;
import com.gumeinteligenciacomercial.orcaja.domain.Plano;
import com.gumeinteligenciacomercial.orcaja.domain.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomOidUserUseCaseTest {

    @Mock
    private UsuarioUseCase usuarioUseCase;

    @Mock
    private OidcUserService delegate; // vamos injetar no campo 'delegate' via ReflectionTestUtils

    @Mock
    private OidcUserRequest userRequest;

    @Mock
    private OidcUser oidcUser;

    @Captor
    private ArgumentCaptor<Usuario> usuarioCaptor;

    private CustomOidUserUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new CustomOidUserUseCase(usuarioUseCase);
        // injeta o mock do OidcUserService no campo 'delegate' (privado e final)
        ReflectionTestUtils.setField(useCase, "delegate", delegate);
    }

    @Test
    void loadUser_quandoUsuarioJaExiste_naoCadastraENaoAltera_retornaOidcUser() {
        // given
        String email = "alice@example.com";
        when(delegate.loadUser(userRequest)).thenReturn(oidcUser);
        when(oidcUser.getEmail()).thenReturn(email);
        when(oidcUser.getAttributes()).thenReturn(Map.of("name", "Alice"));

        // usuário já existe -> consultarPorEmail retorna algo e NÃO lança exception
        when(usuarioUseCase.consultarPorEmail(email))
                .thenReturn(Usuario.builder().id("u1").email(email).build());

        // when
        OidcUser result = useCase.loadUser(userRequest);

        // then
        assertSame(oidcUser, result);
        verify(usuarioUseCase).consultarPorEmail(email);
        verify(usuarioUseCase, never()).cadastrar(any());
    }

    @Test
    void loadUser_quandoUsuarioNaoExiste_cadastraComNomeDoAtributoName() {
        // given
        String email = "bob@example.com";
        when(delegate.loadUser(userRequest)).thenReturn(oidcUser);
        when(oidcUser.getEmail()).thenReturn(email);
        when(oidcUser.getAttributes()).thenReturn(Map.of("name", "Bob"));

        // não existe -> lança exception
        when(usuarioUseCase.consultarPorEmail(email))
                .thenThrow(new UsuarioNaoEncontradoException());

        // stub do cadastrar apenas ecoa o argumento
        when(usuarioUseCase.cadastrar(any(Usuario.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        // when
        OidcUser result = useCase.loadUser(userRequest);

        // then
        assertSame(oidcUser, result);
        verify(usuarioUseCase).consultarPorEmail(email);
        verify(usuarioUseCase).cadastrar(usuarioCaptor.capture());

        Usuario salvo = usuarioCaptor.getValue();
        assertEquals("Bob", salvo.getNome());
        assertEquals(email, salvo.getEmail());
        assertEquals(Plano.GRATIS, salvo.getPlano());
        assertNotNull(salvo.getSenha());
        assertFalse(salvo.getSenha().isBlank());
        assertFalse(salvo.getOnboarding());
    }

    @Test
    void loadUser_quandoUsuarioNaoExisteESemNameNoAtributo_cadastraUsandoEmailComoNome() {
        // given
        String email = "carol@example.com";
        when(delegate.loadUser(userRequest)).thenReturn(oidcUser);
        when(oidcUser.getEmail()).thenReturn(email);
        when(oidcUser.getAttributes()).thenReturn(Collections.emptyMap()); // sem "name"

        when(usuarioUseCase.consultarPorEmail(email))
                .thenThrow(new UsuarioNaoEncontradoException());

        when(usuarioUseCase.cadastrar(any(Usuario.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        // when
        OidcUser result = useCase.loadUser(userRequest);

        // then
        assertSame(oidcUser, result);
        verify(usuarioUseCase).consultarPorEmail(email);
        verify(usuarioUseCase).cadastrar(usuarioCaptor.capture());

        Usuario salvo = usuarioCaptor.getValue();
        // fallback do nome para o email
        assertEquals(email, salvo.getNome());
        assertEquals(email, salvo.getEmail());
        assertEquals(Plano.GRATIS, salvo.getPlano());
        assertNotNull(salvo.getSenha());
        assertFalse(salvo.getSenha().isBlank());
        assertFalse(salvo.getOnboarding());
    }

}