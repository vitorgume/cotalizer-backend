package com.gumeinteligenciacomercial.orcaja.application.usecase;

import com.gumeinteligenciacomercial.orcaja.application.exceptions.LimiteOrcamentosPlanoException;
import com.gumeinteligenciacomercial.orcaja.domain.Orcamento;
import com.gumeinteligenciacomercial.orcaja.domain.OrcamentoTradicional;
import com.gumeinteligenciacomercial.orcaja.domain.Plano;
import com.gumeinteligenciacomercial.orcaja.domain.Usuario;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrcamentosUseCaseTest {

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
        int limite = 5;
        Usuario usuario = Usuario.builder()
                .id(userId)
                .plano(Plano.builder().id("58e84e1b-b19f-4df0-bc72-a8209fbfaf1d").limite(5).build())
                .quantidadeOrcamentos(limite - 1)
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
        int limite = 5;
        Usuario usuario = Usuario.builder()
                .id(userId)
                .plano(Plano.builder().id("58e84e1b-b19f-4df0-bc72-a8209fbfaf1d").limite(5).build())
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
        int limite = 5;
        Usuario usuario = Usuario.builder()
                .id(userId)
                .plano(Plano.builder().id("58e84e1b-b19f-4df0-bc72-a8209fbfaf1d").limite(5).build())
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
        int limite = 5;
        Usuario usuario = Usuario.builder()
                .id(userId)
                .plano(Plano.builder().id("58e84e1b-b19f-4df0-bc72-a8209fbfaf1d").limite(5).build())
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