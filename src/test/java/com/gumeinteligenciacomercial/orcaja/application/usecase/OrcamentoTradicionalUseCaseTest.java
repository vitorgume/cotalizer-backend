package com.gumeinteligenciacomercial.orcaja.application.usecase;

import com.gumeinteligenciacomercial.orcaja.application.exceptions.OrcamentoTradicionalNaoEncontradoException;
import com.gumeinteligenciacomercial.orcaja.application.gateway.OrcamentoTradicionalGateway;
import com.gumeinteligenciacomercial.orcaja.application.gateway.UsuarioGateway;
import com.gumeinteligenciacomercial.orcaja.domain.OrcamentoTradicional;
import com.gumeinteligenciacomercial.orcaja.domain.ProdutoOrcamento;
import com.gumeinteligenciacomercial.orcaja.domain.TipoOrcamento;
import com.gumeinteligenciacomercial.orcaja.domain.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrcamentoTradicionalUseCaseTest {

    @Mock
    private OrcamentoTradicionalGateway gateway;

    @Mock
    private UsuarioUseCase usuarioUseCase;

    @InjectMocks
    private OrcamentoTradicionalUseCase useCase;

    @Captor
    private ArgumentCaptor<OrcamentoTradicional> orcCaptor;

    private OrcamentoTradicional novo;

    @BeforeEach
    void setup() {
        ProdutoOrcamento p1 = ProdutoOrcamento.builder()
                .descricao("A")
                .quantidade(2)
                .valor(BigDecimal.valueOf(10.0))
                .build();
        ProdutoOrcamento p2 = ProdutoOrcamento.builder()
                .descricao("B")
                .quantidade(1)
                .valor(BigDecimal.valueOf(20.0))
                .build();
        novo = OrcamentoTradicional.builder()
                .id("id123")
                .produtos(List.of(p1, p2))
                .build();
    }

    @Test
    void cadastrarDeveConfigurarCamposSalvarERetornarGateway() {
        String usuarioId = "user-123";

        OrcamentoTradicional salvo = OrcamentoTradicional.builder()
                .id("id123")
                .idUsuario(usuarioId) // <-- ESSENCIAL
                .produtos(novo.getProdutos())
                .valorTotal(BigDecimal.valueOf(40.0))
                .tipoOrcamento(TipoOrcamento.TRADICIONAL)
                .dataCriacao(LocalDate.now())
                .build();

        when(gateway.salvar(any(OrcamentoTradicional.class))).thenReturn(salvo);
        when(usuarioUseCase.consultarPorId(eq(usuarioId)))
                .thenReturn(Usuario.builder().id(usuarioId).quantidadeOrcamentos(0).build());

        OrcamentoTradicional result = useCase.cadastrar(novo);

        assertSame(salvo, result);

        verify(gateway).salvar(orcCaptor.capture());
        OrcamentoTradicional toSave = orcCaptor.getValue();

        // Comparação de BigDecimal mais robusta:
        assertEquals(0, BigDecimal.valueOf(40.0).compareTo(toSave.getValorTotal()));
        assertEquals(TipoOrcamento.TRADICIONAL, toSave.getTipoOrcamento());
        assertEquals(LocalDate.now(), toSave.getDataCriacao());

        ArgumentCaptor<Usuario> usuarioCaptor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioUseCase).alterar(eq(usuarioId), usuarioCaptor.capture());
        assertEquals(1, usuarioCaptor.getValue().getQuantidadeOrcamentos());
    }

    @Test
    void consultarPorIdQuandoExistsRetornaOrcamento() {
        when(gateway.consultarPorId("id123")).thenReturn(Optional.of(novo));

        OrcamentoTradicional result = useCase.consultarPorId("id123");

        assertSame(novo, result);
        verify(gateway).consultarPorId("id123");
    }

    @Test
    void consultarPorIdQuandoNaoExistsLancaExcecao() {
        when(gateway.consultarPorId("id123")).thenReturn(Optional.empty());

        assertThrows(OrcamentoTradicionalNaoEncontradoException.class,
                () -> useCase.consultarPorId("id123")
        );
        verify(gateway).consultarPorId("id123");
    }

    @Test
    void alterarDeveBuscarSalvarERetornarAtualizado() {
        OrcamentoTradicional existente = OrcamentoTradicional.builder()
                .id("id123")
                .produtos(List.of())
                .build();
        OrcamentoTradicional atualizado = OrcamentoTradicional.builder()
                .id("id123")
                .produtos(novo.getProdutos())
                .build();
        when(gateway.consultarPorId("id123")).thenReturn(Optional.of(existente));
        when(gateway.salvar(existente)).thenReturn(atualizado);

        OrcamentoTradicional result = useCase.alterar("id123", novo);

        assertSame(atualizado, result);
        verify(gateway).consultarPorId("id123");
        verify(gateway).salvar(existente);
        assertEquals(novo.getProdutos(), existente.getProdutos());
    }

    @Test
    void listarPorUsuarioDeveDelegarParaGateway() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<OrcamentoTradicional> page = new PageImpl<>(List.of(novo));
        when(gateway.listarPorUsuario("user1", pageable)).thenReturn(page);

        Page<OrcamentoTradicional> result = useCase.listarPorUsuario("user1", pageable);

        assertSame(page, result);
        verify(gateway).listarPorUsuario("user1", pageable);
    }

    @Test
    void deletarQuandoExistsDeveChamarGateway() {
        when(gateway.consultarPorId("id123")).thenReturn(Optional.of(novo));

        useCase.deletar("id123");

        verify(gateway).consultarPorId("id123");
        verify(gateway).deletar("id123");
    }

    @Test
    void deletarQuandoNaoExistsLancaExcecao() {
        when(gateway.consultarPorId("id123")).thenReturn(Optional.empty());

        assertThrows(OrcamentoTradicionalNaoEncontradoException.class,
                () -> useCase.deletar("id123")
        );
        verify(gateway).consultarPorId("id123");
        verify(gateway, never()).deletar(any());
    }
}