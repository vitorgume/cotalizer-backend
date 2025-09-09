package com.gumeinteligenciacomercial.orcaja.application.usecase;

import com.gumeinteligenciacomercial.orcaja.application.exceptions.LimiteOrcamentosPlanoException;
import com.gumeinteligenciacomercial.orcaja.application.exceptions.OrcamentoNaoEncontradoException;
import com.gumeinteligenciacomercial.orcaja.application.gateway.OrcamentoGateway;
import com.gumeinteligenciacomercial.orcaja.application.gateway.OrcamentoTradicionalGateway;
import com.gumeinteligenciacomercial.orcaja.application.usecase.ia.IaUseCase;
import com.gumeinteligenciacomercial.orcaja.domain.Orcamento;
import com.gumeinteligenciacomercial.orcaja.domain.OrcamentoTradicional;
import com.gumeinteligenciacomercial.orcaja.domain.Plano;
import com.gumeinteligenciacomercial.orcaja.domain.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrcamentoIaUseCaseTest {

    @Mock UsuarioUseCase usuarioUseCase;
    @Mock OrcamentoIaUseCase orcamentoIaUseCase;
    @Mock OrcamentoTradicionalUseCase orcamentoTradicionalUseCase;

    @InjectMocks
    OrcamentosUseCase useCase;

    // ===== cadastrarOrcamentoIa =====
    @Test
    void cadastrarOrcamentoIa_quandoAbaixoDoLimite_deveConsultarUsuarioEChamarCadastrar() {
        // given
        String userId = "u1";
        int limite = Plano.GRATIS.getLimiteOrcamentos();
        Usuario usuario = Usuario.builder()
                .id(userId)
                .plano(Plano.GRATIS)
                .quantidadeOrcamentos(limite - 1) // abaixo do limite
                .build();

        Orcamento novo = Orcamento.builder().usuarioId(userId).build();
        Orcamento esperado = Orcamento.builder().id("o-ia-1").usuarioId(userId).build();

        when(usuarioUseCase.consultarPorId(userId)).thenReturn(usuario);
        when(orcamentoIaUseCase.cadastrar(novo)).thenReturn(esperado);

        // when
        Orcamento res = useCase.cadastrarOrcamentoIa(novo);

        // then
        assertSame(esperado, res);
        verify(usuarioUseCase).consultarPorId(userId);
        verify(orcamentoIaUseCase).cadastrar(novo);
        verifyNoInteractions(orcamentoTradicionalUseCase);
    }

    @Test
    void cadastrarOrcamentoIa_quandoNoLimite_deveLancarLimiteOrcamentosPlanoException() {
        // given
        String userId = "u2";
        int limite = Plano.GRATIS.getLimiteOrcamentos();
        Usuario usuario = Usuario.builder()
                .id(userId)
                .plano(Plano.GRATIS)
                .quantidadeOrcamentos(limite) // exatamente no limite -> lança
                .build();

        Orcamento novo = Orcamento.builder().usuarioId(userId).build();
        when(usuarioUseCase.consultarPorId(userId)).thenReturn(usuario);

        // when & then
        assertThrows(LimiteOrcamentosPlanoException.class,
                () -> useCase.cadastrarOrcamentoIa(novo));

        verify(usuarioUseCase).consultarPorId(userId);
        verifyNoInteractions(orcamentoIaUseCase, orcamentoTradicionalUseCase);
    }

    // ===== cadastrarOrcamentoTradicional =====
    @Test
    void cadastrarOrcamentoTradicional_quandoAbaixoDoLimite_deveConsultarUsuarioEChamarCadastrar() {
        // given
        String userId = "u3";
        int limite = Plano.GRATIS.getLimiteOrcamentos();
        Usuario usuario = Usuario.builder()
                .id(userId)
                .plano(Plano.GRATIS)
                .quantidadeOrcamentos(limite - 1)
                .build();

        OrcamentoTradicional novo = OrcamentoTradicional.builder().idUsuario(userId).build();
        OrcamentoTradicional esperado = OrcamentoTradicional.builder().id("o-trad-1").idUsuario(userId).build();

        when(usuarioUseCase.consultarPorId(userId)).thenReturn(usuario);
        when(orcamentoTradicionalUseCase.cadastrar(novo)).thenReturn(esperado);

        // when
        OrcamentoTradicional res = useCase.cadastrarOrcamentoTradicional(novo);

        // then
        assertSame(esperado, res);
        verify(usuarioUseCase).consultarPorId(userId);
        verify(orcamentoTradicionalUseCase).cadastrar(novo);
        verifyNoInteractions(orcamentoIaUseCase);
    }

    @Test
    void cadastrarOrcamentoTradicional_quandoNoLimite_deveLancarLimiteOrcamentosPlanoException() {
        // given
        String userId = "u4";
        int limite = Plano.GRATIS.getLimiteOrcamentos();
        Usuario usuario = Usuario.builder()
                .id(userId)
                .plano(Plano.GRATIS)
                .quantidadeOrcamentos(limite) // exatamente no limite -> lança
                .build();

        OrcamentoTradicional novo = OrcamentoTradicional.builder().idUsuario(userId).build();
        when(usuarioUseCase.consultarPorId(userId)).thenReturn(usuario);

        // when & then
        assertThrows(LimiteOrcamentosPlanoException.class,
                () -> useCase.cadastrarOrcamentoTradicional(novo));

        verify(usuarioUseCase).consultarPorId(userId);
        verifyNoInteractions(orcamentoTradicionalUseCase, orcamentoIaUseCase);
    }
}