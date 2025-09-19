package com.gumeinteligenciacomercial.orcaja.application.usecase;

import com.gumeinteligenciacomercial.orcaja.application.exceptions.ArquivoException;
import com.gumeinteligenciacomercial.orcaja.domain.CampoPersonalizado;
import com.gumeinteligenciacomercial.orcaja.domain.OrcamentoTradicional;
import com.gumeinteligenciacomercial.orcaja.domain.ProdutoOrcamento;
import com.gumeinteligenciacomercial.orcaja.domain.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HtmlUseCaseTest {

    @Mock
    private UsuarioUseCase usuarioUseCase;

    @InjectMocks
    private HtmlUseCase htmlUseCase;

    @TempDir
    Path tempDir;

    private Usuario usuarioSemLogo;
    private Usuario usuarioComLogo;
    private Path logoFile;

    @BeforeEach
    void setup() throws IOException {
        usuarioSemLogo = Usuario.builder()
                .id("user1")
                .urlLogo(null)
                .build();

        // cria arquivo temporário com conteúdo "hello" para logo
        logoFile = tempDir.resolve("logo.png");
        Files.writeString(logoFile, "hello", StandardCharsets.UTF_8);
        usuarioComLogo = Usuario.builder()
                .id("user2")
                .urlLogo(logoFile.toString())
                .build();
    }

    @Test
    void gerarHtmlDeveIncluirCamposEItensESemLogo() {
        Map<String, Object> orc = Map.of(
                "campo_uno", "valor1",
                "desconto", 10,
                "itens", List.of(
                        Map.of(
                                "produto", "ProdA",
                                "quantidade", 2,
                                "valor_unit", 5.0
                        )
                )
        );
        when(usuarioUseCase.consultarPorId("user1")).thenReturn(usuarioSemLogo);

        String html = htmlUseCase.gerarHtml(orc, "user1");

        assertTrue(Pattern.compile("\\d{2}:\\d{2} - \\d{2}/\\d{2}/\\d{4}")
                .matcher(html).find(), "Deve conter data formatada");

        assertTrue(html.contains("<p><strong>Campo Uno:</strong> valor1</p>"));

        assertTrue(html.contains("<strong>ProdA</strong>"));
        assertTrue(html.contains("2"));
        assertTrue(html.contains("R$ 5,00"));
        assertTrue(html.contains("R$ 10,00"));

        assertTrue(html.contains("R$ 10,00"));
        assertTrue(html.contains("R$ 1,00"));
        assertTrue(html.contains("R$ 9,00"));
    }

    @Test
    void gerarHtmlTradicionalComCamposEProdutosDeveGerarHtml() {
        OrcamentoTradicional trad = OrcamentoTradicional.builder()
                .id("T1")
                .idUsuario("user1")
                .cliente("Cliente X")
                .cnpjCpf("123")
                .observacoes("Obs")
                .camposPersonalizados(List.of(
                        CampoPersonalizado.builder().titulo("T1").valor("V1").build()
                ))
                .produtos(List.of(
                        ProdutoOrcamento.builder().descricao("Desc").quantidade(3).valor(BigDecimal.valueOf(2.5)).build()
                ))
                .build();
        when(usuarioUseCase.consultarPorId("user1")).thenReturn(usuarioSemLogo);

        String html = htmlUseCase.gerarHtmlTradicional(trad);

        assertTrue(html.contains("T1")); // id
        assertTrue(html.contains("Cliente X"));
        assertTrue(html.contains("123"));
        assertTrue(html.contains("Obs"));
        assertTrue(html.contains("<strong>T1:</strong> V1"));
        assertTrue(html.contains("Desc"));
        assertTrue(html.contains("3"));
        assertTrue(html.contains("R$ 2,50"));
        assertTrue(html.contains("R$ 7,50"));
        assertTrue(html.contains("R$ 7,50")); // subtotal == total
    }

    @Test
    void gerarHtml_deveIgnorarCampoQuandoValorEhListaMesmoNaoSendoItens() {
        Map<String, Object> orc = new java.util.HashMap<>();
        orc.put("tags", java.util.List.of("a","b")); // deve ser ignorado
        orc.put("itens", java.util.List.of());       // vazio, sem linhas
        when(usuarioUseCase.consultarPorId("user1")).thenReturn(usuarioSemLogo);

        String html = htmlUseCase.gerarHtml(orc, "user1");

        // "Tags" viria de formatarChave("tags") -> "Tags"
        assertFalse(html.contains("<p><strong>Tags:</strong>"));
    }

    @Test
    void gerarHtml_quandoUsaDescricaoEValorUnitarioSemDesconto_eLogoDataUri() {
        String dataUri = "data:image/png;base64,SGVsbG8="; // "hello" em base64
        Usuario u = Usuario.builder().id("userX").urlLogo(dataUri).build();

        Map<String, Object> orc = Map.of(
                "cliente", "Fulano",
                "itens", java.util.List.of(
                        Map.of("descricao", "Desc A",
                                "quantidade", "2",
                                "valorUnitario", "3.5")
                )
        );

        when(usuarioUseCase.consultarPorId("userX")).thenReturn(u);

        String html = htmlUseCase.gerarHtml(orc, "userX");

        // logo inline deve aparecer
        assertTrue(html.contains(dataUri));
        // total = 2 * 3,5 = 7,00
        assertTrue(html.contains("R$ 7,00"));
        // como não mandamos "desconto", ele deve ser 0
        assertTrue(html.contains("R$ 0,00"));
        // campos fora de "itens" devem aparecer
        assertTrue(html.contains("<p><strong>Cliente:</strong> Fulano</p>"));
        // descrição deve ser a "descricao" (não "produto")
        assertTrue(html.contains("<strong>Desc A</strong>"));
    }

    @Test
    void gerarHtml_quandoItensAusentes_deveLancarArquivoException() {
        Map<String, Object> orc = Map.of("campo_x", "y"); // sem "itens"

        ArquivoException ex = assertThrows(ArquivoException.class,
                () -> htmlUseCase.gerarHtml(orc, "user1"));
        assertEquals("Erro ao gerar html para orçamento com IA.", ex.getMessage());
    }

    @Test
    void gerarHtml_quandoUsuarioUseCaseFalha_deveLancarArquivoException() {
        Map<String, Object> orc = Map.of("itens", java.util.List.of());
        when(usuarioUseCase.consultarPorId("user1"))
                .thenThrow(new IllegalStateException("boom"));

        ArquivoException ex = assertThrows(ArquivoException.class,
                () -> htmlUseCase.gerarHtml(orc, "user1"));
        assertEquals("Erro ao gerar html para orçamento com IA.", ex.getMessage());
    }

    @Test
    void gerarHtmlTradicional_semCamposNemProdutos_eLogoDataUri_deveGerarSubtotalETotalZero() {
        String dataUri = "data:image/png;base64,QUJD"; // "ABC"
        Usuario u = Usuario.builder().id("u-t0").urlLogo(dataUri).build();
        when(usuarioUseCase.consultarPorId("u-t0")).thenReturn(u);

        OrcamentoTradicional trad = OrcamentoTradicional.builder()
                .id("T0")
                .idUsuario("u-t0")
                .cliente("Cli")
                .cnpjCpf("000")
                .observacoes("Obs")
                .camposPersonalizados(null) // branch: null
                .produtos(null)             // branch: null
                .build();

        String html = htmlUseCase.gerarHtmlTradicional(trad);

        assertTrue(html.contains(dataUri));
        assertTrue(html.contains("R$ 0,00")); // subtotal
        // total também 0,00
        assertTrue(html.contains("R$ 0,00"));
        // campos básicos
        assertTrue(html.contains("Cli"));
        assertTrue(html.contains("000"));
        assertTrue(html.contains("Obs"));
    }

    @Test
    void gerarHtmlTradicional_quandoUsuarioUseCaseFalha_deveLancarArquivoException() {
        OrcamentoTradicional trad = OrcamentoTradicional.builder()
                .id("T2")
                .idUsuario("bad")
                .cliente("C")
                .cnpjCpf("1")
                .observacoes("O")
                .build();

        when(usuarioUseCase.consultarPorId("bad"))
                .thenThrow(new RuntimeException("x"));

        ArquivoException ex = assertThrows(ArquivoException.class,
                () -> htmlUseCase.gerarHtmlTradicional(trad));
        assertEquals("Erro ao gerar html tradicional.", ex.getMessage());
    }

    @Test
    void gerarHtmlTradicional_deveEscaparHtmlEmCampos() {
        Usuario u = Usuario.builder().id("u-esc").urlLogo(null).build();
        when(usuarioUseCase.consultarPorId("u-esc")).thenReturn(u);

        CampoPersonalizado cp = CampoPersonalizado.builder()
                .titulo("A&B<>'\"")
                .valor("<b>VAL</b>&")
                .build();

        ProdutoOrcamento p = ProdutoOrcamento.builder()
                .descricao("<X&>")
                .quantidade(1)
                .valor(BigDecimal.valueOf(2))
                .build();

        OrcamentoTradicional trad = OrcamentoTradicional.builder()
                .id("T-esc")
                .idUsuario("u-esc")
                .cliente("Cli & < > \"")
                .cnpjCpf("<123>")
                .observacoes("Obs & \" < >")
                .camposPersonalizados(java.util.List.of(cp))
                .produtos(java.util.List.of(p))
                .build();

        String html = htmlUseCase.gerarHtmlTradicional(trad);

        assertTrue(html.contains("Cli &amp; &lt; &gt; &quot;"));
        assertTrue(html.contains("&lt;123&gt;"));
        assertTrue(html.contains("Obs &amp; &quot; &lt; &gt;"));

        assertTrue(html.contains("<p><strong>A&amp;B&lt;&gt;'&quot;:</strong> &lt;b&gt;VAL&lt;/b&gt;&amp;</p>"));

        assertTrue(html.contains("&lt;X&amp;&gt;"));
    }


    @Test
    void gerarHtml_deveEmbutirLogoQuandoHttp200() throws Exception {
        var http = com.sun.net.httpserver.HttpServer.create(new InetSocketAddress(0), 0);
        http.createContext("/logo", exchange -> {
            byte[] body = "PNG!".getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "image/png");
            exchange.sendResponseHeaders(200, body.length);
            try (var os = exchange.getResponseBody()) { os.write(body); }
        });
        http.start();
        int port = http.getAddress().getPort();
        String url = "http://localhost:" + port + "/logo";

        var usuario = Usuario.builder().id("u1").urlLogo(url).build();
        when(usuarioUseCase.consultarPorId("u1")).thenReturn(usuario);

        Map<String,Object> orc = Map.of(
                "itens", List.of(Map.of("produto","X","quantidade",1,"valor_unit",1))
        );

        String html = new HtmlUseCase(usuarioUseCase).gerarHtml(orc, "u1");
        assertTrue(html.contains("data:image/png;base64,"));

        http.stop(0);
    }

    @Test
    void gerarHtml_quandoLogo404_retornaSemDataUri() throws Exception {
        var http = com.sun.net.httpserver.HttpServer.create(new InetSocketAddress(0), 0);
        http.createContext("/logo404", exchange -> {
            exchange.sendResponseHeaders(404, -1);
            exchange.close();
        });
        http.start();
        int port = http.getAddress().getPort();
        String url = "http://localhost:" + port + "/logo404";

        var usuario = Usuario.builder().id("u1").urlLogo(url).build();
        when(usuarioUseCase.consultarPorId("u1")).thenReturn(usuario);

        Map<String,Object> orc = Map.of("itens", List.of());
        String html = new HtmlUseCase(usuarioUseCase).gerarHtml(orc, "u1");
        assertFalse(html.contains("data:image/"));

        http.stop(0);
    }
}