package com.gumeinteligenciacomercial.orcaja.application.usecase;

import com.gumeinteligenciacomercial.orcaja.application.gateway.ArquivoGateway;
import com.gumeinteligenciacomercial.orcaja.domain.Orcamento;
import com.gumeinteligenciacomercial.orcaja.domain.OrcamentoTradicional;
import com.gumeinteligenciacomercial.orcaja.domain.Template;
import com.gumeinteligenciacomercial.orcaja.domain.Usuario;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class   ArquivoUseCaseTest {

    @Mock
    OrcamentoIaUseCase orcamentoIaUseCase;

    @Mock
    OrcamentoTradicionalUseCase orcamentoTradicionalUseCase;

    @Mock
    UsuarioUseCase usuarioUseCase;

    @Mock
    ArquivoGateway gateway;

    @Mock
    HtmlUseCase htmlUseCase;

    @InjectMocks
    ArquivoUseCase sut;

    @Captor
    ArgumentCaptor<String> nomeArquivoCaptor;

    @Captor
    ArgumentCaptor<String> htmlCaptor;

    @Captor
    ArgumentCaptor<Orcamento> orcamentoCaptor;

    @Captor
    ArgumentCaptor<OrcamentoTradicional> orcTradicionalCaptor;

    @Captor
    ArgumentCaptor<Usuario> usuarioCaptor;

    private static final Pattern NOME_ARQ_PATTERN = Pattern.compile("^ARQ-[A-Za-z0-9]{5}$");
    private static final String TEMPLATE_TEST = "teste";

    private Orcamento novoOrcamento(String id, String usuarioId) {
        return Orcamento.builder()
                .id(id)
                .usuarioId(usuarioId)
                .titulo("Titulo")
                .conteudoOriginal("original")
                .orcamentoFormatado(Map.of("key", "value"))
                .dataCriacao(LocalDate.now())
                .valorTotal(new BigDecimal("123.45"))
                .template(Template.builder().id("teste").nomeArquivo("teste").build())
                .build();
    }

    private OrcamentoTradicional novoOrcamentoTradicional(String id, String idUsuario) {
        return OrcamentoTradicional.builder()
                .id(id)
                .cliente("Cliente X")
                .cnpjCpf("00.000.000/0000-00")
                .observacoes("Obs padrão")
                .idUsuario(idUsuario)
                .valorTotal(new BigDecimal("321.00"))
                .dataCriacao(LocalDate.now())
                .template(Template.builder().id("teste").nomeArquivo("teste").build())
                .build();
    }

    private Usuario novoUsuario(String id, String email) {
        return Usuario.builder()
                .id(id).email(email).nome("Fulano")
                .telefone("44-9999-0000")
                .build();
    }

    @Test
    void salvarArquivoDeveGerarSalvarEAtualizarOrcamento() {
        var input = novoOrcamento("orc-1", "usr-1");

        when(htmlUseCase.gerarHtml(input.getOrcamentoFormatado(), input.getUsuarioId(), TEMPLATE_TEST))
                .thenReturn("<html>OK</html>");
        when(gateway.salvarPdf(anyString(), eq("<html>OK</html>")))
                .thenReturn("https://files/ARQ-abcde.pdf");

        var existente = novoOrcamento("orc-1", "usr-1");
        when(orcamentoIaUseCase.consultarPorId("orc-1")).thenReturn(existente);
        when(orcamentoIaUseCase.alterar(eq("orc-1"), any(Orcamento.class)))
                .thenAnswer(inv -> inv.getArgument(1, Orcamento.class));

        var out = sut.salvarArquivo(input);

        assertNotNull(out);
        assertEquals("https://files/ARQ-abcde.pdf", out.getUrlArquivo());

        InOrder inOrder = inOrder(htmlUseCase, gateway, orcamentoIaUseCase);
        inOrder.verify(htmlUseCase).gerarHtml(input.getOrcamentoFormatado(), input.getUsuarioId(), TEMPLATE_TEST);
        inOrder.verify(gateway).salvarPdf(nomeArquivoCaptor.capture(), htmlCaptor.capture());
        inOrder.verify(orcamentoIaUseCase).consultarPorId("orc-1");
        inOrder.verify(orcamentoIaUseCase).alterar(eq("orc-1"), orcamentoCaptor.capture());

        assertEquals("<html>OK</html>", htmlCaptor.getValue());
        assertTrue(NOME_ARQ_PATTERN.matcher(nomeArquivoCaptor.getValue()).matches(),
                "nome de arquivo deve seguir o pattern ARQ-xxxxx");
        assertEquals("https://files/ARQ-abcde.pdf", orcamentoCaptor.getValue().getUrlArquivo());

        verifyNoMoreInteractions(htmlUseCase, gateway, orcamentoIaUseCase);
    }

    @Test
    void salvarArquivoDevePropagarExcecao_quandoGerarHtmlFalha() {
        var input = novoOrcamento("orc-err", "usr-1");
        when(htmlUseCase.gerarHtml(any(), any(), anyString()))
                .thenThrow(new IllegalStateException("falha html"));

        var ex = assertThrows(IllegalStateException.class, () -> sut.salvarArquivo(input));
        assertEquals("falha html", ex.getMessage());

        verify(htmlUseCase).gerarHtml(input.getOrcamentoFormatado(), input.getUsuarioId(), TEMPLATE_TEST);
        verifyNoInteractions(gateway, orcamentoIaUseCase);
    }

    @Test
    void salvarArquivoDevePropagarExcecaoQuandoSalvarPdfFalha() {
        var input = novoOrcamento("orc-err", "usr-1");
        when(htmlUseCase.gerarHtml(any(), any(), anyString())).thenReturn("<html/>");
        when(gateway.salvarPdf(anyString(), anyString()))
                .thenThrow(new RuntimeException("falha pdf"));

        var ex = assertThrows(RuntimeException.class, () -> sut.salvarArquivo(input));
        assertEquals("falha pdf", ex.getMessage());

        verify(htmlUseCase).gerarHtml(input.getOrcamentoFormatado(), input.getUsuarioId(), TEMPLATE_TEST);
        verify(gateway).salvarPdf(anyString(), eq("<html/>"));
        verifyNoInteractions(orcamentoIaUseCase);
    }

    @Test
    void salvarArquivoDeveLancarNullPointerSeConsultarPorIdRetornaNull() {
        var input = novoOrcamento("orc-x", "usr-1");
        when(htmlUseCase.gerarHtml(any(), any(), anyString())).thenReturn("<html/>");
        when(gateway.salvarPdf(anyString(), anyString())).thenReturn("url");
        when(orcamentoIaUseCase.consultarPorId("orc-x")).thenReturn(null);

        assertThrows(NullPointerException.class, () -> sut.salvarArquivo(input));

        verify(htmlUseCase, times(1)).gerarHtml(input.getOrcamentoFormatado(), input.getUsuarioId(), TEMPLATE_TEST);
        verify(gateway).salvarPdf(anyString(), eq("<html/>"));
        verify(orcamentoIaUseCase).consultarPorId("orc-x");
        verify(orcamentoIaUseCase, never()).alterar(anyString(), any());
    }

    @Test
    void salvarArquivoDevePropagarExcecaoQuandoAlterarFalha() {
        var input = novoOrcamento("orc-2", "usr-1");
        when(htmlUseCase.gerarHtml(any(), any(), anyString())).thenReturn("<html/>");
        when(gateway.salvarPdf(anyString(), anyString())).thenReturn("url");
        when(orcamentoIaUseCase.consultarPorId("orc-2")).thenReturn(novoOrcamento("orc-2", "usr-1"));
        when(orcamentoIaUseCase.alterar(eq("orc-2"), any()))
                .thenThrow(new IllegalStateException("falha alterar"));

        var ex = assertThrows(IllegalStateException.class, () -> sut.salvarArquivo(input));
        assertEquals("falha alterar", ex.getMessage());

        verify(orcamentoIaUseCase).consultarPorId("orc-2");
        verify(orcamentoIaUseCase).alterar(eq("orc-2"), any());
    }

    @Test
    void salvarArquivoTradicionalDeveGerarSalvarEAtualizar() {
        var input = novoOrcamentoTradicional("ot-1", "usr-1");

        when(htmlUseCase.gerarHtmlTradicional(input)).thenReturn("<html>T</html>");
        when(gateway.salvarPdf(anyString(), eq("<html>T</html>")))
                .thenReturn("https://files/ARQ-xxxxx.pdf");

        var existente = novoOrcamentoTradicional("ot-1", "usr-1");
        when(orcamentoTradicionalUseCase.consultarPorId("ot-1")).thenReturn(existente);
        when(orcamentoTradicionalUseCase.alterar(eq("ot-1"), any(OrcamentoTradicional.class)))
                .thenAnswer(inv -> inv.getArgument(1, OrcamentoTradicional.class));

        var out = sut.salvarArquivoTradicional(input);

        assertNotNull(out);
        assertEquals("https://files/ARQ-xxxxx.pdf", out.getUrlArquivo());

        InOrder inOrder = inOrder(htmlUseCase, gateway, orcamentoTradicionalUseCase);
        inOrder.verify(htmlUseCase).gerarHtmlTradicional(input);
        inOrder.verify(gateway).salvarPdf(nomeArquivoCaptor.capture(), htmlCaptor.capture());
        inOrder.verify(orcamentoTradicionalUseCase).consultarPorId("ot-1");
        inOrder.verify(orcamentoTradicionalUseCase).alterar(eq("ot-1"), orcTradicionalCaptor.capture());

        assertTrue(NOME_ARQ_PATTERN.matcher(nomeArquivoCaptor.getValue()).matches(),
                "nome de arquivo deve seguir o pattern ARQ-xxxxx");
        assertEquals("<html>T</html>", htmlCaptor.getValue());
        assertEquals("https://files/ARQ-xxxxx.pdf", orcTradicionalCaptor.getValue().getUrlArquivo());
    }

    @Test
    void salvarArquivoTradicionalDevePropagarExcecaoQuandoHtmlFalha() {
        var input = novoOrcamentoTradicional("ot-err", "usr-1");
        when(htmlUseCase.gerarHtmlTradicional(input)).thenThrow(new IllegalStateException("htmlT"));

        var ex = assertThrows(IllegalStateException.class, () -> sut.salvarArquivoTradicional(input));
        assertEquals("htmlT", ex.getMessage());

        verify(htmlUseCase).gerarHtmlTradicional(input);
        verifyNoInteractions(gateway, orcamentoTradicionalUseCase);
    }

    @Test
    void salvarArquivoTradicionalDevePropagarExcecaoQuandoSalvarPdfFalha() {
        var input = novoOrcamentoTradicional("ot-err", "usr-1");
        when(htmlUseCase.gerarHtmlTradicional(input)).thenReturn("<html/>");
        when(gateway.salvarPdf(anyString(), anyString())).thenThrow(new RuntimeException("pdfT"));

        var ex = assertThrows(RuntimeException.class, () -> sut.salvarArquivoTradicional(input));
        assertEquals("pdfT", ex.getMessage());

        verify(htmlUseCase).gerarHtmlTradicional(input);
        verify(gateway).salvarPdf(anyString(), eq("<html/>"));
        verifyNoInteractions(orcamentoTradicionalUseCase);
    }

    @Test
    void salvarArquivoTradicionalDeveLancarNullPointerSeConsultarPorIdRetornaNull() {
        var input = novoOrcamentoTradicional("ot-x", "usr-1");
        when(htmlUseCase.gerarHtmlTradicional(input)).thenReturn("<html/>");
        when(gateway.salvarPdf(anyString(), anyString())).thenReturn("url");
        when(orcamentoTradicionalUseCase.consultarPorId("ot-x")).thenReturn(null);

        assertThrows(NullPointerException.class, () -> sut.salvarArquivoTradicional(input));

        verify(orcamentoTradicionalUseCase).consultarPorId("ot-x");
        verify(orcamentoTradicionalUseCase, never()).alterar(anyString(), any());
    }

    @Test
    void salvarArquivoTradicionalDevePropagarExcecaoQuandoAlterarFalha() {
        var input = novoOrcamentoTradicional("ot-2", "usr-1");
        when(htmlUseCase.gerarHtmlTradicional(input)).thenReturn("<html/>");
        when(gateway.salvarPdf(anyString(), anyString())).thenReturn("url");
        when(orcamentoTradicionalUseCase.consultarPorId("ot-2")).thenReturn(novoOrcamentoTradicional("ot-2", "usr-1"));
        when(orcamentoTradicionalUseCase.alterar(eq("ot-2"), any()))
                .thenThrow(new IllegalStateException("altT"));

        var ex = assertThrows(IllegalStateException.class, () -> sut.salvarArquivoTradicional(input));
        assertEquals("altT", ex.getMessage());
    }

    @Test
    void cadastrarLogoDeveSalvarAtualizarEDevolverPath() {
        String userId = "u-1";
        MultipartFile file = mock(MultipartFile.class);
        when(usuarioUseCase.consultarPorId(userId)).thenReturn(novoUsuario(userId, "u@x.com"));
        when(gateway.salvarLogo(userId, file)).thenReturn("logos/u-1/logo.png");
        when(usuarioUseCase.alterar(eq(userId), any(Usuario.class)))
                .thenAnswer(inv -> inv.getArgument(1, Usuario.class));

        String path = sut.cadastrarLogo(userId, file);
        assertEquals("logos/u-1/logo.png", path);

        InOrder inOrder = inOrder(usuarioUseCase, gateway, usuarioUseCase);
        inOrder.verify(usuarioUseCase).consultarPorId(userId);
        inOrder.verify(gateway).salvarLogo(userId, file);
        inOrder.verify(usuarioUseCase).alterar(eq(userId), usuarioCaptor.capture());

        assertEquals("logos/u-1/logo.png", usuarioCaptor.getValue().getUrlLogo());
    }

    @Test
    void cadastrarLogoDevePropagarExcecaoQuandoUsuarioNaoExiste() {
        when(usuarioUseCase.consultarPorId("nope"))
                .thenThrow(new NoSuchElementException("not found"));

        assertThrows(NoSuchElementException.class,
                () -> sut.cadastrarLogo("nope", mock(MultipartFile.class)));

        verify(usuarioUseCase).consultarPorId("nope");
        verifyNoInteractions(gateway);
    }

    @Test
    void cadastrarLogoDevePropagarExcecaoQuandoSalvarLogoFalha() {
        String userId = "u-2";
        MultipartFile file = mock(MultipartFile.class);
        when(usuarioUseCase.consultarPorId(userId)).thenReturn(novoUsuario(userId, "u@x.com"));
        when(gateway.salvarLogo(eq(userId), eq(file)))
                .thenThrow(new IllegalStateException("falha logo"));

        var ex = assertThrows(IllegalStateException.class, () -> sut.cadastrarLogo(userId, file));
        assertEquals("falha logo", ex.getMessage());

        verify(usuarioUseCase).consultarPorId(userId);
        verify(gateway).salvarLogo(userId, file);
        verify(usuarioUseCase, never()).alterar(anyString(), any());
    }

    @Test
    void acessarArquivoDeveSanitizarChaveAntesDeCarregar() {
        String sujo = "  /pasta/arquivo.  ";
        String esperado = "pasta/arquivo";
        Resource resource = mock(Resource.class);

        when(gateway.carregarArquivo(esperado)).thenReturn(resource);

        var out = sut.acessarArquivo(sujo);
        assertSame(resource, out);

        verify(gateway).carregarArquivo(esperado);
    }

    @Test
    void downloadArquivoNaoDeveSanitizarChave() {
        String key = "  /pasta/arquivo.  ";
        Resource resource = mock(Resource.class);
        when(gateway.carregarArquivo(key)).thenReturn(resource);

        var out = sut.downloadArquivo(key);
        assertSame(resource, out);

        verify(gateway).carregarArquivo(key);
    }

    @Test
    void deveDeletarArquivoComSucesso() {
        String key = "/pasta/arquivo.";
        Mockito.doNothing().when(gateway).deletarArquivo(anyString());

        sut.deletaArquivo(key);

        verify(gateway).deletarArquivo(anyString());
    }

    @Test
    void deveDeletarLogoComSucesso() {
        String key = "/pasta/logo.";
        Mockito.doNothing().when(gateway).deletarLogo(anyString());

        sut.deletarLogo(key);

        verify(gateway).deletarLogo(anyString());
    }

    @Test
    void acessarArquivo_quandoComecaComBarraApenas_removeBarraInicial() {
        String sujo = "   /dir/arquivo.txt   ";
        String esperado = "dir/arquivo.txt";
        Resource resource = mock(Resource.class);

        when(gateway.carregarArquivo(esperado)).thenReturn(resource);

        var out = sut.acessarArquivo(sujo);

        assertSame(resource, out);
        verify(gateway).carregarArquivo(esperado);
    }

    @Test
    void acessarArquivo_quandoTerminaComPontoApenas_removePontoFinal() {
        String sujo = "dir/sub/arquivo.   ";
        String esperado = "dir/sub/arquivo";
        Resource resource = mock(Resource.class);

        when(gateway.carregarArquivo(esperado)).thenReturn(resource);

        var out = sut.acessarArquivo(sujo);

        assertSame(resource, out);
        verify(gateway).carregarArquivo(esperado);
    }

    @Test
    void acessarArquivo_quandoNaoPrecisaSanitizar_mantemChave() {
        String sujo = "dir/sub/arquivo";
        String esperado = "dir/sub/arquivo";
        Resource resource = mock(Resource.class);

        when(gateway.carregarArquivo(esperado)).thenReturn(resource);

        var out = sut.acessarArquivo(sujo);

        assertSame(resource, out);
        verify(gateway).carregarArquivo(esperado);
    }

}