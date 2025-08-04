package com.gumeinteligenciacomercial.orcaja.infrastructure.dataprovider;

import com.gumeinteligenciacomercial.orcaja.infrastructure.exceptions.DataProviderException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.springframework.web.multipart.MultipartFile;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ArquivoDataProviderTest {

    private ArquivoDataProvider provider;

    @BeforeEach
    void setUp() {
        provider = new ArquivoDataProvider();
    }

    @Test
    void salvarPdfDeveRetornarUrlEInvocarRenderer() throws Exception {
        String nome = "meuArquivo";
        String html = "<html><body>Teste</body></html>";

        try (MockedConstruction<ITextRenderer> rendMocks =
                     mockConstruction(ITextRenderer.class);
             MockedConstruction<FileOutputStream> fosMocks =
                     mockConstruction(FileOutputStream.class)) {

            String url = provider.salvarPdf(nome, html);

            assertEquals(
                    "http://localhost:8080/arquivos/acessar/" + nome + ".pdf",
                    url
            );

            ITextRenderer renderer = rendMocks.constructed().get(0);
            verify(renderer).setDocumentFromString(html);
            verify(renderer).layout();
            verify(renderer).createPDF(any(OutputStream.class));
        }
    }

    @Test
    void salvarPdfQuandoRendererLancarErroDeveLancarDataProviderException() {
        String nome = "arquivoErro";
        String html = "<html></html>";

        try (MockedConstruction<ITextRenderer> rendMocks =
                     mockConstruction(ITextRenderer.class, (mock, ctx) -> {
                         doThrow(new RuntimeException("fail")).when(mock).layout();
                     });
             MockedConstruction<FileOutputStream> fosMocks =
                     mockConstruction(FileOutputStream.class)) {

            DataProviderException ex = assertThrows(
                    DataProviderException.class,
                    () -> provider.salvarPdf(nome, html)
            );
            assertEquals("Erro ao gerar PDF", ex.getMessage());
        }
    }


    @Test
    void salvarLogoDeveCriarDiretorioETransferirArquivo() throws Exception {
        String userId = "usr123";
        MultipartFile multipart = mock(MultipartFile.class);

        when(multipart.getOriginalFilename()).thenReturn("logoTeste");
        when(multipart.getOriginalFilename())
                .thenReturn("logoTeste.png");

        Path userDir = Paths.get("C:/Users/vitor/orcaja", userId);
        Path esperado = userDir.resolve("logoTeste.png");

        try (MockedStatic<Files> files = mockStatic(Files.class)) {
            files.when(() -> Files.exists(userDir)).thenReturn(false);
            files.when(() -> Files.createDirectories(userDir))
                    .thenReturn(userDir);

            String resultado = provider.salvarLogo(userId, multipart);

            assertEquals(esperado.toString(), resultado);
            verify(multipart).transferTo(esperado.toFile());
        }
    }

    @Test
    void salvarLogoQuandoTransferToLancarErroDeveLancarDataProviderException() throws Exception {
        String userId = "usrErr";
        MultipartFile multipart = mock(MultipartFile.class);

        when(multipart.getOriginalFilename()).thenReturn("img");
        Path userDir = Paths.get("C:/Users/vitor/orcaja", userId);
        Path destino = userDir.resolve("img.png");

        try (MockedStatic<Files> files = mockStatic(Files.class)) {
            files.when(() -> Files.exists(userDir)).thenReturn(true);

            doThrow(new IOException("disk error"))
                    .when(multipart).transferTo(destino.toFile());

            DataProviderException ex = assertThrows(
                    DataProviderException.class,
                    () -> provider.salvarLogo(userId, multipart)
            );
            assertEquals("Erro ao salvar logo", ex.getMessage());
        }
    }
}