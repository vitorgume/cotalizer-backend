package com.gumeinteligenciacomercial.orcaja.application.usecase;

import com.gumeinteligenciacomercial.orcaja.application.exceptions.LimiteOrcamentosPlanoException;
import com.gumeinteligenciacomercial.orcaja.application.exceptions.OrcamentoNaoEncontradoException;
import com.gumeinteligenciacomercial.orcaja.application.gateway.OrcamentoGateway;
import com.gumeinteligenciacomercial.orcaja.application.gateway.OrcamentoTradicionalGateway;
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
class OrcamentoIaUseCaseTest {

    @Mock
    private OrcamentoGateway gateway;

    @Mock
    private OrcamentoTradicionalGateway orcamentoTradicionalGateway;

    @Mock
    private IaUseCase iaUseCase;

    @Mock
    private UsuarioUseCase usuarioUseCase;

    @InjectMocks
    private OrcamentoIaUseCase useCase;

    private final String usuarioId = "user1";

    @BeforeEach
    void init() {

    }

    @Test
    void consultarPorIdExistenteRetornaOrcamento() {
        Orcamento o = Orcamento.builder().id("X").build();
        when(gateway.consultarPorId("X")).thenReturn(Optional.of(o));

        Orcamento result = useCase.consultarPorId("X");
        assertSame(o, result);
        verify(gateway).consultarPorId("X");
    }

    @Test
    void consultarPorIdNaoExisteLancaException() {
        when(gateway.consultarPorId("Z")).thenReturn(Optional.empty());
        assertThrows(OrcamentoNaoEncontradoException.class, () -> useCase.consultarPorId("Z"));
        verify(gateway).consultarPorId("Z");
    }

    @Test
    void listarPorUsuarioDelegaParaGateway() {
        Pageable pg = PageRequest.of(0, 10);
        Page<Orcamento> page = new PageImpl<>(List.of(Orcamento.builder().build()));
        when(gateway.listarPorUsuario(usuarioId, pg)).thenReturn(page);

        Page<Orcamento> result = useCase.listarPorUsuario(usuarioId, pg);
        assertSame(page, result);
        verify(gateway).listarPorUsuario(usuarioId, pg);
    }

    @Test
    void deletarExistenteChamaGateway() {
        when(gateway.consultarPorId("A")).thenReturn(Optional.of(Orcamento.builder().id("C").build()));

        useCase.deletar("A");
        verify(gateway).consultarPorId("A");
        verify(gateway).deletar("A");
    }

    @Test
    void deletarNaoExisteLancaException() {
        when(gateway.consultarPorId("B")).thenReturn(Optional.empty());
        assertThrows(OrcamentoNaoEncontradoException.class, () -> useCase.deletar("B"));
        verify(gateway).consultarPorId("B");
        verify(gateway, never()).deletar(any());
    }

    @Test
    void alterarExistenteAtualizaEDaRetorno() {
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
}