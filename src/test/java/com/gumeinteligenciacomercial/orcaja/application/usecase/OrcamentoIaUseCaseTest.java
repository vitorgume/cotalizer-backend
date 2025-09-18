package com.gumeinteligenciacomercial.orcaja.application.usecase;

import com.gumeinteligenciacomercial.orcaja.application.exceptions.OrcamentoNaoEncontradoException;
import com.gumeinteligenciacomercial.orcaja.application.gateway.OrcamentoGateway;
import com.gumeinteligenciacomercial.orcaja.application.usecase.ia.IaUseCase;
import com.gumeinteligenciacomercial.orcaja.domain.Orcamento;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrcamentoIaUseCaseTest {

    @Mock
    private OrcamentoGateway gateway;

    @Mock
    private IaUseCase iaUseCase;

    @Mock
    private UsuarioUseCase usuarioUseCase;

    @InjectMocks
    private OrcamentoIaUseCase useCase;

    @Captor
    private ArgumentCaptor<Orcamento> orcCaptor;

    private Orcamento novo;
    private final String usuarioId = "user-123";

    @BeforeEach
    void setup() {
        novo = Orcamento.builder()
                .id("orc-1")
                .usuarioId(usuarioId)
                .conteudoOriginal("texto livre do orçamento")
                .build();
    }

    private Map<String, Object> gerarMapaIAComDescontoPercentual() {
        Map<String, Object> item1 = new HashMap<>();
        item1.put("descricao", "A");
        item1.put("quantidade", 2);
        item1.put("valor_unit", 10.00);

        Map<String, Object> item2 = new HashMap<>();
        item2.put("descricao", "B");
        item2.put("quantidade", 1);
        item2.put("valor_unit", 20.00);

        Map<String, Object> mapa = new HashMap<>();
        mapa.put("itens", List.of(item1, item2));
        mapa.put("desconto", "10%");

        return mapa;
    }

    private Map<String, Object> gerarMapaIAComDescontoAbsoluto() {
        Map<String, Object> item = new HashMap<>();
        item.put("descricao", "Único");
        item.put("quantidade", 3);
        item.put("valor_unit", 50);

        Map<String, Object> mapa = new HashMap<>();
        mapa.put("itens", List.of(item));
        mapa.put("desconto", "15.50");
        return mapa;
    }

    @Test
    void cadastrar_deve_gerarCalcularESalvar_incrementandoUsoUsuario_descontoPercentual() {
        Map<String, Object> iaMap = gerarMapaIAComDescontoPercentual();

        Orcamento salvo = Orcamento.builder()
                .id("orc-1")
                .usuarioId(usuarioId)
                .conteudoOriginal(novo.getConteudoOriginal())
                .dataCriacao(LocalDate.now())
                .tipoOrcamento(TipoOrcamento.IA)
                .valorTotal(BigDecimal.valueOf(36.00))
                .orcamentoFormatado(new HashMap<>())
                .build();

        when(iaUseCase.gerarOrcamento(anyString())).thenReturn(iaMap);
        when(gateway.salvar(any(Orcamento.class))).thenReturn(salvo);
        when(usuarioUseCase.consultarPorId(eq(usuarioId)))
                .thenReturn(Usuario.builder().id(usuarioId).quantidadeOrcamentos(0).build());

        Orcamento result = useCase.cadastrar(novo);

        assertSame(salvo, result);

        verify(gateway).salvar(orcCaptor.capture());
        Orcamento paraSalvar = orcCaptor.getValue();

        assertEquals(TipoOrcamento.IA, paraSalvar.getTipoOrcamento());
        assertEquals(LocalDate.now(), paraSalvar.getDataCriacao());

        assertEquals(0, BigDecimal.valueOf(36.00).compareTo(paraSalvar.getValorTotal()));

        ArgumentCaptor<Usuario> usuarioCaptor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioUseCase).alterar(eq(usuarioId), usuarioCaptor.capture());
        assertEquals(1, usuarioCaptor.getValue().getQuantidadeOrcamentos());
    }

    @Test
    void cadastrar_deve_calcularTotal_quandoDescontoAbsoluto() {
        Map<String, Object> iaMap = gerarMapaIAComDescontoAbsoluto();

        Orcamento salvo = Orcamento.builder()
                .id("orc-1")
                .usuarioId(usuarioId)
                .conteudoOriginal(novo.getConteudoOriginal())
                .dataCriacao(LocalDate.now())
                .tipoOrcamento(TipoOrcamento.IA)
                .valorTotal(new BigDecimal("134.50"))
                .orcamentoFormatado(new HashMap<>())
                .build();

        when(iaUseCase.gerarOrcamento(anyString())).thenReturn(iaMap);
        when(gateway.salvar(any(Orcamento.class))).thenReturn(salvo);
        when(usuarioUseCase.consultarPorId(eq(usuarioId)))
                .thenReturn(Usuario.builder().id(usuarioId).quantidadeOrcamentos(2).build());

        Orcamento result = useCase.cadastrar(novo);
        assertSame(salvo, result);

        verify(gateway).salvar(orcCaptor.capture());
        Orcamento enviado = orcCaptor.getValue();

        // subtotal=150; desconto=15.50; total=134.50
        assertEquals(0, new BigDecimal("134.50").compareTo(enviado.getValorTotal()));
        assertEquals(TipoOrcamento.IA, enviado.getTipoOrcamento());

        // usuário somou +1 (de 2 para 3) — aqui validamos que o objeto passado já vem incrementado
        ArgumentCaptor<Usuario> usuarioCaptor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioUseCase).alterar(eq(usuarioId), usuarioCaptor.capture());
        assertEquals(3, usuarioCaptor.getValue().getQuantidadeOrcamentos());
    }

    @Test
    void cadastrar_comentarioSobreRamoDeString() {
        Map<String, Object> iaMap = gerarMapaIAComDescontoPercentual();
        when(iaUseCase.gerarOrcamento(anyString())).thenReturn(iaMap);
        when(gateway.salvar(any(Orcamento.class))).thenAnswer(inv -> inv.getArgument(0));
        when(usuarioUseCase.consultarPorId(eq(usuarioId)))
                .thenReturn(Usuario.builder().id(usuarioId).quantidadeOrcamentos(0).build());

        Orcamento salvo = useCase.cadastrar(novo);

        assertTrue(salvo.getValorTotal() instanceof BigDecimal);
    }

    @Test
    void consultarPorId_quandoExiste_retornaOrcamento() {
        when(gateway.consultarPorId("o1")).thenReturn(Optional.of(novo));

        Orcamento result = useCase.consultarPorId("o1");

        assertSame(novo, result);
        verify(gateway).consultarPorId("o1");
    }

    @Test
    void consultarPorId_quandoNaoExiste_lancaExcecao() {
        when(gateway.consultarPorId("o1")).thenReturn(Optional.empty());

        assertThrows(OrcamentoNaoEncontradoException.class,
                () -> useCase.consultarPorId("o1"));
        verify(gateway).consultarPorId("o1");
    }

    @Test
    void listarPorUsuario_deveDelegarParaGateway() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Orcamento> page = new PageImpl<>(List.of(novo));
        when(gateway.listarPorUsuario(usuarioId, pageable)).thenReturn(page);

        Page<Orcamento> result = useCase.listarPorUsuario(usuarioId, pageable);

        assertSame(page, result);
        verify(gateway).listarPorUsuario(usuarioId, pageable);
    }

    @Test
    void deletar_quandoExiste_deveChamarGateway() {
        when(gateway.consultarPorId("o1")).thenReturn(Optional.of(novo));

        useCase.deletar("o1");

        verify(gateway).consultarPorId("o1");
        verify(gateway).deletar("o1");
    }

    @Test
    void deletar_quandoNaoExiste_lancaExcecao_eNaoDeleta() {
        when(gateway.consultarPorId("o1")).thenReturn(Optional.empty());

        assertThrows(OrcamentoNaoEncontradoException.class,
                () -> useCase.deletar("o1"));

        verify(gateway).consultarPorId("o1");
        verify(gateway, never()).deletar(anyString());
    }

    @Test
    void alterar_deveBuscar_salvar_eRetornarAtualizado() {
        Orcamento existente = Orcamento.builder()
                .id("o1")
                .usuarioId(usuarioId)
                .conteudoOriginal("antigo")
                .build();

        Orcamento novoEstado = Orcamento.builder()
                .id("o1")
                .usuarioId(usuarioId)
                .conteudoOriginal("novo")
                .build();

        Orcamento atualizado = Orcamento.builder()
                .id("o1")
                .usuarioId(usuarioId)
                .conteudoOriginal("novo") // após setDados(...)
                .build();

        when(gateway.consultarPorId("o1")).thenReturn(Optional.of(existente));
        when(gateway.salvar(any(Orcamento.class))).thenReturn(atualizado);

        Orcamento result = useCase.alterar("o1", novoEstado);

        assertSame(atualizado, result);
        verify(gateway).consultarPorId("o1");
        verify(gateway).salvar(existente); // o próprio objeto recuperado é salvo
    }

}