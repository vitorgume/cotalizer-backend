package com.gumeinteligenciacomercial.orcaja.entrypoint.controller.arquivo;

import com.gumeinteligenciacomercial.orcaja.application.usecase.ArquivoUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(
        properties = {
                "spring.task.scheduling.enabled=false",
                "openia.api.key=TEST_OPENAI_KEY",
                "security.api.key=TEST_SIGNATURES_KEY",
                "secret.key=SECRET_KEY_TEST"
        }
)
@AutoConfigureMockMvc(addFilters = false)
public class ArquivoControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ArquivoUseCase arquivoUseCase;

    @Test
    void acessarArquivo_deveRetornarInlinePdf() throws Exception {
        byte[] data = "pdf content".getBytes();
        Resource res = new ByteArrayResource(data) {
            @Override
            public String getFilename() {
                return "file.pdf";
            }
        };
        when(arquivoUseCase.acessarArquivo("file.pdf")).thenReturn(res);

        mockMvc.perform(get("/arquivos/acessar/file.pdf"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "inline; filename=file.pdf"))
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(content().bytes(data));

        then(arquivoUseCase).should().acessarArquivo("file.pdf");
    }

    @Test
    void downloadArquivo_deveRetornarAttachmentPdf() throws Exception {
        byte[] data = "pdf data".getBytes();
        Resource res = new ByteArrayResource(data) {
            @Override
            public String getFilename() {
                return "doc.pdf";
            }
        };
        given(arquivoUseCase.downloadArquivo("doc.pdf")).willReturn(res);

        mockMvc.perform(get("/arquivos/download/doc.pdf"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=doc.pdf"))
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(content().bytes(data));

        then(arquivoUseCase).should().downloadArquivo("doc.pdf");
    }
}
