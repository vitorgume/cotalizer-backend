package com.gumeinteligenciacomercial.orcaja.application.usecase;

import com.gumeinteligenciacomercial.orcaja.application.exceptions.ArquivoException;
import com.gumeinteligenciacomercial.orcaja.application.exceptions.ArquivoNaoEncontrado;
import com.gumeinteligenciacomercial.orcaja.application.gateway.ArquivoGateway;
import com.gumeinteligenciacomercial.orcaja.domain.Orcamento;
import com.gumeinteligenciacomercial.orcaja.domain.OrcamentoTradicional;
import com.gumeinteligenciacomercial.orcaja.domain.Usuario;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArquivoUseCaseTest {

    @Mock
    private OrcamentoIaUseCase orcamentoIaUseCase;

    @Mock
    private OrcamentoTradicionalUseCase orcamentoTradicionalUseCase;

    @Mock
    private UsuarioUseCase usuarioUseCase;

    @Mock
    private ArquivoGateway gateway;

    @Mock
    private HtmlUseCase htmlUseCase;

    @InjectMocks
    private ArquivoUseCase arquivoUseCase;

    @Captor
    private ArgumentCaptor<String> nomeCaptor;

    @Captor
    private ArgumentCaptor<String> htmlCaptor;

    @Captor
    private ArgumentCaptor<Orcamento> orcCaptor;

    @Captor
    private ArgumentCaptor<OrcamentoTradicional> tradCaptor;

    @Captor
    private ArgumentCaptor<String> idCaptor;

    @Captor
    private ArgumentCaptor<Usuario> usuarioCaptor;

    @Captor
    private ArgumentCaptor<MultipartFile> fileCaptor;

    @Test
    void salvarArquivoDeveGerarPdfEAtualizarOrcamento() {
        Orcamento novo = Orcamento.builder()
                .id("1")
                .orcamentoFormatado(Map.of("fmt", "fmt"))
                .usuarioId("u1")
                .build();
        when(htmlUseCase.gerarHtml(Map.of("fmt", "fmt"),"u1")).thenReturn("<html/>");
        when(gateway.salvarPdf(anyString(), eq("<html/>"))).thenReturn("http://host/file.pdf");

        Orcamento existente = Orcamento.builder().id("1").build();
        when(orcamentoIaUseCase.consultarPorId("1")).thenReturn(existente);

        Orcamento salvo = Orcamento.builder()
                .id("1")
                .urlArquivo("http://host/file.pdf")
                .build();
        when(orcamentoIaUseCase.alterar("1", existente)).thenReturn(salvo);

        Orcamento result = arquivoUseCase.salvarArquivo(novo);

        assertSame(salvo, result);
        verify(htmlUseCase).gerarHtml(Map.of("fmt", "fmt"),"u1");
        verify(gateway).salvarPdf(nomeCaptor.capture(), htmlCaptor.capture());
        String geradoHtml = htmlCaptor.getValue();
        assertEquals("<html/>", geradoHtml);
        assertTrue(nomeCaptor.getValue().startsWith("ARQ-"));
        verify(orcamentoIaUseCase).consultarPorId("1");
        verify(orcamentoIaUseCase).alterar(eq("1"), eq(existente));
    }

    @Test
    void salvarArquivoTradicionalDeveGerarPdfEAtualizarOrcamentoTradicional() {
        OrcamentoTradicional novo = OrcamentoTradicional.builder()
                .id("T1")
                .build();
        when(htmlUseCase.gerarHtmlTradicional(novo)).thenReturn("<htmlT/>");
        when(gateway.salvarPdf(anyString(), eq("<htmlT/>"))).thenReturn("http://host/fileT.pdf");

        OrcamentoTradicional existente = OrcamentoTradicional.builder().id("T1").build();
        when(orcamentoTradicionalUseCase.consultarPorId("T1")).thenReturn(existente);

        OrcamentoTradicional salvo = OrcamentoTradicional.builder()
                .id("T1")
                .urlArquivo("http://host/fileT.pdf")
                .build();
        when(orcamentoTradicionalUseCase.alterar("T1", existente)).thenReturn(salvo);

        OrcamentoTradicional result = arquivoUseCase.salvarArquivoTradicional(novo);

        assertSame(salvo, result);
        verify(htmlUseCase).gerarHtmlTradicional(novo);
        verify(gateway).salvarPdf(nomeCaptor.capture(), eq("<htmlT/>"));
        assertTrue(nomeCaptor.getValue().startsWith("ARQ-"));
        verify(orcamentoTradicionalUseCase).consultarPorId("T1");
        verify(orcamentoTradicionalUseCase).alterar(eq("T1"), eq(existente));
    }

    @Test
    void cadastrarLogoDeveSalvarLogoEAtualizarUsuario() {
        String userId = "U100";
        Usuario usuario = Usuario.builder()
                .id(userId)
                .build();
        when(usuarioUseCase.consultarPorId(userId)).thenReturn(usuario);
        MultipartFile file = mock(MultipartFile.class);
        when(gateway.salvarLogo(userId, file)).thenReturn("logos/u100.png");

        String path = arquivoUseCase.cadastrarLogo(userId, file);

        assertEquals("logos/u100.png", path);
        verify(usuarioUseCase).consultarPorId(userId);
        verify(gateway).salvarLogo(userId, file);
        verify(usuarioUseCase).alterar(eq(userId), usuarioCaptor.capture());
        assertEquals("logos/u100.png", usuarioCaptor.getValue().getKeyLogo());
    }

    @Test
    void acessarArquivoQuandoNaoExisteDeveLancarArquivoNaoEncontrado() {
        String fake = "no-this-file.txt";
        assertThrows(ArquivoNaoEncontrado.class,
                () -> arquivoUseCase.acessarArquivo(fake));
    }

    @Test
    void acessarArquivoComNomeNullDeveLancarArquivoException() {
        assertThrows(ArquivoException.class,
                () -> arquivoUseCase.acessarArquivo(null));
    }

    @Test
    void downloadArquivoQuandoNaoExisteDeveLancarArquivoNaoEncontrado() {
        assertThrows(ArquivoNaoEncontrado.class,
                () -> arquivoUseCase.downloadArquivo("absent.pdf"));
    }

    @Test
    void downloadArquivoComNomeNullDeveLancarArquivoException() {
        assertThrows(ArquivoException.class,
                () -> arquivoUseCase.downloadArquivo(null));
    }
}