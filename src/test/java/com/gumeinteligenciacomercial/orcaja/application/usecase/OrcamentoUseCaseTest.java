package com.gumeinteligenciacomercial.orcaja.application.usecase;

import com.gumeinteligenciacomercial.orcaja.application.exceptions.LimiteOrcamentosPlano;
import com.gumeinteligenciacomercial.orcaja.application.exceptions.OrcamentoNaoEncontradoException;
import com.gumeinteligenciacomercial.orcaja.application.gateway.OrcamentoGateway;
import com.gumeinteligenciacomercial.orcaja.application.usecase.ia.IaUseCase;
import com.gumeinteligenciacomercial.orcaja.domain.Orcamento;
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
class OrcamentoUseCaseTest {

    @Mock
    private OrcamentoGateway gateway;

    @Mock
    private IaUseCase iaUseCase;

    @Mock
    private UsuarioUseCase usuarioUseCase;

    @InjectMocks
    private OrcamentoUseCase useCase;

    private final String usuarioId = "user1";

    @BeforeEach
    void init() {

    }

    @Test
    void consultarPorId_existente_retornaOrcamento() {
        Orcamento o = Orcamento.builder().id("X").build();
        when(gateway.consultarPorId("X")).thenReturn(Optional.of(o));

        Orcamento result = useCase.consultarPorId("X");
        assertSame(o, result);
        verify(gateway).consultarPorId("X");
    }

    @Test
    void consultarPorId_naoExiste_lancaException() {
        when(gateway.consultarPorId("Z")).thenReturn(Optional.empty());
        assertThrows(OrcamentoNaoEncontradoException.class, () -> useCase.consultarPorId("Z"));
        verify(gateway).consultarPorId("Z");
    }

    @Test
    void listarPorUsuario_delegaParaGateway() {
        Pageable pg = PageRequest.of(0, 10);
        Page<Orcamento> page = new PageImpl<>(List.of(Orcamento.builder().build()));
        when(gateway.listarPorUsuario(usuarioId, pg)).thenReturn(page);

        Page<Orcamento> result = useCase.listarPorUsuario(usuarioId, pg);
        assertSame(page, result);
        verify(gateway).listarPorUsuario(usuarioId, pg);
    }

    @Test
    void deletar_existente_chamaGateway() {
        when(gateway.consultarPorId("A")).thenReturn(Optional.of(Orcamento.builder().id("C").build()));

        useCase.deletar("A");
        verify(gateway).consultarPorId("A");
        verify(gateway).deletar("A");
    }

    @Test
    void deletar_naoExiste_lancaException() {
        when(gateway.consultarPorId("B")).thenReturn(Optional.empty());
        assertThrows(OrcamentoNaoEncontradoException.class, () -> useCase.deletar("B"));
        verify(gateway).consultarPorId("B");
        verify(gateway, never()).deletar(any());
    }

    @Test
    void alterar_existente_atualizaEDaRetorno() {
        Orcamento orig = Orcamento.builder().id("C").build();
        Orcamento novo = Orcamento.builder().id("C").build();

        novo.setDataCriacao(LocalDate.of(2000,1,1));
        when(gateway.consultarPorId("C")).thenReturn(Optional.of(orig));
        when(gateway.salvar(orig)).thenReturn(novo);

        Orcamento result = useCase.alterar("C", Orcamento.builder().build());
        assertSame(novo, result);
        assertEquals(novo.getDataCriacao(), result.getDataCriacao());
        verify(gateway).consultarPorId("C");
        verify(gateway).salvar(orig);
    }

    @Test
    void cadastrar_limitePlanoFreeAtingido_lancaLimiteOrcamentosPlano() {
        Usuario free = Usuario.builder()
                .id(usuarioId)
                .plano(Plano.GRATIS)
                .build();
        when(usuarioUseCase.consultarPorId(usuarioId)).thenReturn(free);

        Pageable pg = PageRequest.of(0, 10);
        Page<Orcamento> page2 = new PageImpl<>(List.of(
                Orcamento.builder().id("C").build(),
                Orcamento.builder().id("C").build(),
                Orcamento.builder().id("C").build(),
                Orcamento.builder().id("C").build(),
                Orcamento.builder().id("C").build(),
                Orcamento.builder().id("C").build(),
                Orcamento.builder().id("C").build(),
                Orcamento.builder().id("C").build(),
                Orcamento.builder().id("C").build(),
                Orcamento.builder().id("C").build()
        ));
        when(gateway.listarPorUsuario(usuarioId, pg)).thenReturn(page2);

        Orcamento dummy = Orcamento.builder().usuarioId(usuarioId).build();

        assertThrows(LimiteOrcamentosPlano.class, () -> useCase.cadastrar(dummy));

        verify(usuarioUseCase).consultarPorId(usuarioId);
        verify(gateway).listarPorUsuario(usuarioId, pg);
        verifyNoInteractions(iaUseCase);
    }
}