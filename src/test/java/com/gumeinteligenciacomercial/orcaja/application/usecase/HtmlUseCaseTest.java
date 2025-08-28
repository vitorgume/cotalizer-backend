package com.gumeinteligenciacomercial.orcaja.application.usecase;

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

//    @Test
//    void gerarHtmlDeveIncluirBase64DoLogo() {
//        // Arrange
//        Map<String, Object> orc = Map.of(
//                "campo_uno", "valor1",
//                "itens", List.of()
//        );
//        when(usuarioUseCase.consultarPorId("user2")).thenReturn(usuarioComLogo);
//
//        // Act
//        String html = htmlUseCase.gerarHtml(orc, "user2");
//
//        // Assert
//        String base64 = Base64.getEncoder().encodeToString("hello".getBytes(StandardCharsets.UTF_8));
//        assertTrue(html.contains(base64), "Deve conter logo em Base64");
//    }

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
}