package com.gumeinteligenciacomercial.orcaja.application.usecase.google;

import com.gumeinteligenciacomercial.orcaja.application.exceptions.UsuarioNaoEncontradoException;
import com.gumeinteligenciacomercial.orcaja.application.usecase.PlanoUseCase;
import com.gumeinteligenciacomercial.orcaja.application.usecase.UsuarioUseCase;
import com.gumeinteligenciacomercial.orcaja.domain.Plano;
import com.gumeinteligenciacomercial.orcaja.domain.Usuario;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomOAuth2UserUseCaseTest {

    @Mock
    private UsuarioUseCase usuarioUseCase;

    @Mock
    private OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate;

    @InjectMocks
    private CustomOAuth2UserUseCase useCase;

    @Mock
    private OAuth2UserRequest userRequest;

    @Mock
    private OAuth2User oauth2User;

    @Mock
    private PlanoUseCase planoUseCase;

    @Captor
    private ArgumentCaptor<Usuario> usuarioCaptor;

    @Test
    void loadUserUsuarioExistenteSomenteConsulta() {
        String email = "maria@example.com";
        String nome  = "Maria Silva";

        when(delegate.loadUser(userRequest)).thenReturn(oauth2User);
        when(oauth2User.getAttribute("email")).thenReturn(email);
        when(oauth2User.getAttribute("name")).thenReturn(nome);

        Usuario existing = Usuario.builder()
                .email(email)
                .nome(nome)
                .senha("qualquer")
                .build();
        when(usuarioUseCase.consultarPorEmail(email)).thenReturn(existing);

        OAuth2User returned = useCase.loadUser(userRequest);

        assertSame(oauth2User, returned);
        verify(usuarioUseCase, times(1)).consultarPorEmail(email);
        verify(usuarioUseCase, never()).cadastrar(any());
    }

    @Test
    void loadUserUsuarioNaoExisteCadastraNovoUsuario() {
        String email = "joao@example.com";
        String nome  = "João Souza";

        when(delegate.loadUser(userRequest)).thenReturn(oauth2User);
        when(oauth2User.getAttribute("email")).thenReturn(email);
        when(oauth2User.getAttribute("name")).thenReturn(nome);
        when(planoUseCase.consularPlanoPadrao()).thenReturn(Plano.builder().id("idteste123").build());

        doThrow(new UsuarioNaoEncontradoException())
                .when(usuarioUseCase).consultarPorEmail(email);

        OAuth2User returned = useCase.loadUser(userRequest);

        assertSame(oauth2User, returned);
        verify(usuarioUseCase, times(1)).consultarPorEmail(email);
        verify(usuarioUseCase, times(1)).cadastrar(usuarioCaptor.capture());

        Usuario novo = usuarioCaptor.getValue();
        assertEquals(nome,  novo.getNome());
        assertEquals(email, novo.getEmail());
        assertNotNull(novo.getSenha());
    }
}