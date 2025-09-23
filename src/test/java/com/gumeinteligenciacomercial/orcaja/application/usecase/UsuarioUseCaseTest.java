package com.gumeinteligenciacomercial.orcaja.application.usecase;

import com.gumeinteligenciacomercial.orcaja.application.exceptions.CodigoInvalidoAlteracaoSenha;
import com.gumeinteligenciacomercial.orcaja.application.exceptions.CodigoInvalidoValidacaoEmailException;
import com.gumeinteligenciacomercial.orcaja.application.exceptions.UsuarioJaCadastradoException;
import com.gumeinteligenciacomercial.orcaja.application.exceptions.UsuarioNaoEncontradoException;
import com.gumeinteligenciacomercial.orcaja.application.gateway.UsuarioGateway;
import com.gumeinteligenciacomercial.orcaja.domain.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
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
                .tipoCadastro(TipoCadastro.TRADICIONAL)
                .build();
        when(criptografiaUseCase.criptografar("raw")).thenReturn("hashed");
        Usuario saved = Usuario.builder().id(userId).build();
        when(gateway.salvar(captor.capture())).thenReturn(saved);

        useCase.cadastrar(u);

        Usuario result = captor.getValue();

        assertEquals("hashed", u.getSenha());
        verify(criptografiaUseCase).criptografar("raw");
        verify(emailUseCase, never()).enviarCodigoVerificacao(anyString(), anyString());
        verify(gateway).salvar(u);
        assertEquals(StatusUsuario.PENDENTE_VALIDACAO_EMAIL, result.getStatus());
        assertEquals(Plano.GRATIS, result.getPlano());
    }

    @Test
    void cadastrarJaExistenteLancaUsuarioJaCadastradoException() {
        Usuario exists = Usuario.builder().email("emailteste").build();
        when(gateway.consultarPorEmail("emailteste")).thenReturn(Optional.of(exists));
        Usuario toCreate = Usuario.builder().email("emailteste").build();

        assertThrows(UsuarioJaCadastradoException.class,
                () -> useCase.cadastrar(toCreate)
        );
        verify(gateway).consultarPorEmail("emailteste");
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
    void inativarExistenteAlteraStatusParaInativo() {
        Usuario u = Usuario.builder().build();
        when(gateway.consultarPorId(userId)).thenReturn(Optional.of(u));
        Usuario saved = Usuario.builder()
                .id(userId)
                .status(StatusUsuario.INATIVO)
                .build();
        when(gateway.salvar(any(Usuario.class))).thenReturn(saved);

        Usuario result = useCase.inativar(userId);

        assertSame(saved, result);
        assertEquals(StatusUsuario.INATIVO, result.getStatus());
        verify(gateway).consultarPorId(userId);
        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(gateway).salvar(captor.capture());
        assertEquals(StatusUsuario.INATIVO, captor.getValue().getStatus());
    }

    @Test
    void inativarNaoExisteLancaUsuarioNaoEncontradoException() {
        when(gateway.consultarPorId(userId)).thenReturn(Optional.empty());

        assertThrows(UsuarioNaoEncontradoException.class,
                () -> useCase.inativar(userId)
        );

        verify(gateway).consultarPorId(userId);
        verify(gateway, never()).salvar(any());
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

    @Test
    void ajustarQuantidadeOrcamentoMensal_quandoHaUsuarios_zeraContadorEAlteraCadaUm() {
        Usuario u1 = Usuario.builder()
                .id("u1").email("a@x.com").quantidadeOrcamentos(5).build();
        Usuario u2 = Usuario.builder()
                .id("u2").email("b@x.com").quantidadeOrcamentos(2).build();

        when(gateway.listar()).thenReturn(List.of(u1, u2));

        when(gateway.consultarPorId("u1")).thenReturn(Optional.of(u1));
        when(gateway.consultarPorId("u2")).thenReturn(Optional.of(u2));

        when(gateway.salvar(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        useCase.ajustarQuantidadeOrcamentoMensal();

        verify(gateway).listar();

        verify(gateway).consultarPorId("u1");
        verify(gateway).consultarPorId("u2");

        verify(gateway, times(2)).salvar(captor.capture());
        List<Usuario> salvos = captor.getAllValues();
        assertEquals(2, salvos.size());
        assertEquals(0, salvos.get(0).getQuantidadeOrcamentos());
        assertEquals(0, salvos.get(1).getQuantidadeOrcamentos());

        assertEquals(0, u1.getQuantidadeOrcamentos());
        assertEquals(0, u2.getQuantidadeOrcamentos());
    }

    @Test
    void ajustarQuantidadeOrcamentoMensalQuandoListaVaziaNaoSalvaNada() {
        when(gateway.listar()).thenReturn(List.of());

        useCase.ajustarQuantidadeOrcamentoMensal();

        verify(gateway).listar();
        verify(gateway, never()).consultarPorId(anyString());
        verify(gateway, never()).salvar(any());
    }

    @Test
    void alterarSenha_quandoValidadorRetornaNull_naoCriptografaMasAindaSalva() {
        String code = "c-null";
        when(codigoAlteracaoSenhaUseCase.validaCodigoAlteracaoSenha(code)).thenReturn(null);

        Usuario u = Usuario.builder().id(null).senha("old").build();
        when(gateway.consultarPorId(isNull())).thenReturn(Optional.of(u));

        when(gateway.salvar(u)).thenReturn(u);

        Usuario res = useCase.alterarSenha("nova", code);

        assertSame(u, res);
        verify(criptografiaUseCase, never()).criptografar(anyString());
        verify(codigoAlteracaoSenhaUseCase).validaCodigoAlteracaoSenha(code);
        verify(gateway).consultarPorId(isNull());
        verify(gateway).salvar(u);
    }

    @Test
    void reenviarCodigoEmail_quandoUsuarioNaoExiste_devePropagarExceptionESemEnvio() {
        String email = "no@x";
        when(gateway.consultarPorEmail(email)).thenReturn(Optional.empty());

        assertThrows(UsuarioNaoEncontradoException.class,
                () -> useCase.reenviarCodigoEmail(email));

        verify(gateway).consultarPorEmail(email);
        verifyNoInteractions(codigoValidacaoUseCase, emailUseCase);
    }
}