package com.gumeinteligenciacomercial.orcaja.application.usecase;

import com.gumeinteligenciacomercial.orcaja.application.exceptions.CodigoInvalidoAlteracaoSenha;
import com.gumeinteligenciacomercial.orcaja.application.exceptions.CodigoInvalidoValidacaoEmailException;
import com.gumeinteligenciacomercial.orcaja.application.exceptions.UsuarioJaCadastradoException;
import com.gumeinteligenciacomercial.orcaja.application.exceptions.UsuarioNaoEncontradoException;
import com.gumeinteligenciacomercial.orcaja.application.gateway.UsuarioGateway;
import com.gumeinteligenciacomercial.orcaja.domain.Plano;
import com.gumeinteligenciacomercial.orcaja.domain.StatusUsuario;
import com.gumeinteligenciacomercial.orcaja.domain.Usuario;
import com.gumeinteligenciacomercial.orcaja.domain.VerificacaoEmail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioUseCaseTest {

    @Mock
    private UsuarioGateway gateway;
    @Mock
    private CriptografiaUseCase criptografiaUseCase;
    @Mock
    private EmailUseCase emailUseCase;
    @Mock
    private CodigoValidacaoUseCase codigoValidacaoUseCase;
    @Mock
    private CodigoAlteracaoSenhaUseCase codigoAlteracaoSenhaUseCase;

    @InjectMocks
    private UsuarioUseCase useCase;

    private final String userId = "u1";

    @Captor
    private ArgumentCaptor<Usuario> captor;

    @Test
    void cadastrarNovoUsuarioComCpfECnpjChamaSalvarSemValidacaoEmail() {
        Usuario u = Usuario.builder()
                .email("e@x.com")
                .senha("raw")
                .cpf("123")
                .cnpj("456")
                .build();
        when(criptografiaUseCase.criptografar("raw")).thenReturn("hashed");
        Usuario saved = Usuario.builder().id(userId).build();
        when(gateway.salvar(captor.capture())).thenReturn(saved);
        when(gateway.consultarPorCpf(anyString())).thenReturn(Optional.empty());

        useCase.cadastrar(u);

        Usuario result = captor.getValue();

        assertEquals("hashed", u.getSenha());
        verify(gateway).consultarPorCpf("123");
        verify(criptografiaUseCase).criptografar("raw");
        verify(emailUseCase, never()).enviarCodigoVerificacao(anyString(), anyString());
        verify(gateway).salvar(u);
        assertEquals(StatusUsuario.PENDENTE_VALIDACAO_EMAIL, result.getStatus());
        assertEquals(Plano.GRATIS, result.getPlano());
    }

    @Test
    void cadastrarJaExistenteLancaUsuarioJaCadastradoException() {
        Usuario exists = Usuario.builder().cpf("123").build();
        when(gateway.consultarPorCpf("123")).thenReturn(Optional.of(exists));
        Usuario toCreate = Usuario.builder().cpf("123").build();

        assertThrows(UsuarioJaCadastradoException.class,
                () -> useCase.cadastrar(toCreate)
        );
        verify(gateway).consultarPorCpf("123");
        verifyNoMoreInteractions(gateway);
    }

    // === consultarPorId ===
    @Test
    void consultarPorIdExistenteRetornaUsuario() {
        Usuario u = Usuario.builder().build();
        when(gateway.consultarPorId("X")).thenReturn(Optional.of(u));

        Usuario res = useCase.consultarPorId("X");
        assertSame(u, res);
        verify(gateway).consultarPorId("X");
    }

    @Test
    void consultarPorIdNaoExisteLancaException() {
        when(gateway.consultarPorId("X")).thenReturn(Optional.empty());
        assertThrows(UsuarioNaoEncontradoException.class,
                () -> useCase.consultarPorId("X")
        );
        verify(gateway).consultarPorId("X");
    }

    @Test
    void deletarExistenteChamaGateway() {
        when(gateway.consultarPorId("X")).thenReturn(Optional.of(Usuario.builder().build()));
        useCase.deletar("X");
        verify(gateway).consultarPorId("X");
        verify(gateway).deletar("X");
    }

    @Test
    void deletarNaoExisteLancaUsuarioNaoEncontrado() {
        when(gateway.consultarPorId("X")).thenReturn(Optional.empty());
        assertThrows(UsuarioNaoEncontradoException.class,
                () -> useCase.deletar("X")
        );
        verify(gateway).consultarPorId("X");
        verify(gateway, never()).deletar(anyString());
    }

    @Test
    void consultarPorEmailExistenteRetornaUsuario() {
        Usuario u = Usuario.builder().build();
        when(gateway.consultarPorEmail("e@x.com")).thenReturn(Optional.of(u));
        Usuario res = useCase.consultarPorEmail("e@x.com");
        assertSame(u, res);
        verify(gateway).consultarPorEmail("e@x.com");
    }

    @Test
    void consultarPorEmailNaoExisteLancaException() {
        when(gateway.consultarPorEmail("e@x.com")).thenReturn(Optional.empty());
        assertThrows(UsuarioNaoEncontradoException.class,
                () -> useCase.consultarPorEmail("e@x.com")
        );
        verify(gateway).consultarPorEmail("e@x.com");
    }

    @Test
    void alterarEmailDiferenteDisparaValidacaoEmail() {
        Usuario existing = Usuario.builder().id(userId).email("old@x").build();
        when(gateway.consultarPorId(userId)).thenReturn(Optional.of(existing));
        Usuario novos = Usuario.builder().email("new@x").build();
        when(codigoValidacaoUseCase.gerarCodigo("new@x")).thenReturn("code");
        doNothing().when(emailUseCase).enviarCodigoVerificacao("new@x","code");
        when(gateway.salvar(existing)).thenReturn(existing);

        Usuario res = useCase.alterar(userId, novos);
        assertSame(existing, res);
        verify(codigoValidacaoUseCase).gerarCodigo("new@x");
        verify(emailUseCase).enviarCodigoVerificacao("new@x","code");
        verify(gateway).salvar(existing);
    }

    @Test
    void alterarMesmoEmailNaoValidaEmail() {
        Usuario existing = Usuario.builder().id(userId).email("same@x").build();
        when(gateway.consultarPorId(userId)).thenReturn(Optional.of(existing));
        Usuario novos = Usuario.builder().email("same@x").build();
        when(gateway.salvar(existing)).thenReturn(existing);

        Usuario res = useCase.alterar(userId, novos);
        assertSame(existing, res);
        verify(codigoValidacaoUseCase, never()).gerarCodigo(anyString());
        verify(emailUseCase, never()).enviarCodigoVerificacao(anyString(), anyString());
        verify(gateway).salvar(existing);
    }

    @Test
    void validarCodigoVerificacaoValidoRetornaVerificacaoEmail() {
        String email = "u@x";
        String code = "123";
        Usuario u = Usuario.builder().id(userId).email(email).build();
        when(codigoValidacaoUseCase.validar(email, code)).thenReturn(true);
        when(gateway.consultarPorEmail(email)).thenReturn(Optional.of(u));
        when(gateway.consultarPorId(userId)).thenReturn(Optional.of(u));
        when(gateway.salvar(u)).thenReturn(u);

        VerificacaoEmail ve = useCase.validarCodigoVerificacao(email, code);
        assertEquals(email, ve.getEmail());
        verify(codigoValidacaoUseCase).validar(email, code);
        verify(gateway).consultarPorEmail(email);
        verify(gateway).consultarPorId(userId);
        verify(gateway).salvar(u);
    }

    @Test
    void validarCodigoVerificacaoInvalidoLancaException() {
        String email = "u@x";
        String code = "123";
        when(codigoValidacaoUseCase.validar(email, code)).thenReturn(false);
        assertThrows(CodigoInvalidoValidacaoEmailException.class,
                () -> useCase.validarCodigoVerificacao(email, code)
        );
        verify(codigoValidacaoUseCase).validar(email, code);
        verifyNoMoreInteractions(gateway);
    }

    @Test
    void reenviarCodigoEmailSucessoDeveGerarEEnviarCodigo() {
        String email = "u@x";
        Usuario u = Usuario.builder().email(email).build();
        when(gateway.consultarPorEmail(email)).thenReturn(Optional.of(u));
        when(codigoValidacaoUseCase.gerarCodigo(email)).thenReturn("c");
        doNothing().when(emailUseCase).enviarCodigoVerificacao(email, "c");

        useCase.reenviarCodigoEmail(email);
        verify(gateway).consultarPorEmail(email);
        verify(codigoValidacaoUseCase).gerarCodigo(email);
        verify(emailUseCase).enviarCodigoVerificacao(email, "c");
    }

    @Test
    void alterarSenhaValidoAtualizaESalva() {
        String code = "c";
        String newPass = "p";
        when(codigoAlteracaoSenhaUseCase.validaCodigoAlteracaoSenha(code)).thenReturn(userId);
        Usuario u = Usuario.builder().id(userId).senha("old").build();
        when(gateway.consultarPorId(userId)).thenReturn(Optional.of(u));
        when(criptografiaUseCase.criptografar(newPass)).thenReturn("nh");
        when(gateway.salvar(u)).thenReturn(u);

        Usuario res = useCase.alterarSenha(newPass, code);
        assertEquals("nh", u.getSenha());
        verify(codigoAlteracaoSenhaUseCase).validaCodigoAlteracaoSenha(code);
        verify(gateway).consultarPorId(userId);
        verify(criptografiaUseCase).criptografar(newPass);
        verify(gateway).salvar(u);
    }

    @Test
    void alterarSenhaCodigoInvalidoLancaException() {
        String code = "c";
        when(codigoAlteracaoSenhaUseCase.validaCodigoAlteracaoSenha(code))
                .thenThrow(new CodigoInvalidoAlteracaoSenha());
        assertThrows(CodigoInvalidoAlteracaoSenha.class,
                () -> useCase.alterarSenha("p", code)
        );
        verify(codigoAlteracaoSenhaUseCase).validaCodigoAlteracaoSenha(code);
        verifyNoMoreInteractions(gateway, criptografiaUseCase, emailUseCase, codigoValidacaoUseCase);
    }
}