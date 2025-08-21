package com.gumeinteligenciacomercial.orcaja.infrastructure.dataprovider;

import com.gumeinteligenciacomercial.orcaja.application.gateway.ArquivoGateway;
import com.gumeinteligenciacomercial.orcaja.infrastructure.exceptions.DataProviderException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

@Component
@Slf4j
public class ArquivoDataProvider implements ArquivoGateway {

    private static final String BASE_PATH = "C:/Users/vitor/orcaja";
    private static final String BASE_API_FILE = "http://localhost:8080/arquivos/acessar/";

    @Override
    public String salvarPdf(String nomeArquivo, String html) {
        try {
            String nomeArquivoCompleto = nomeArquivo + ".pdf";
            String caminhoParaSalvar = Paths.get(BASE_PATH, nomeArquivoCompleto).toString();

            try (OutputStream outputStream = new FileOutputStream(caminhoParaSalvar)) {
                ITextRenderer renderer = new ITextRenderer();
                renderer.setDocumentFromString(html);
                renderer.layout();
                renderer.createPDF(outputStream);
            }

            return BASE_API_FILE + nomeArquivoCompleto;
        } catch (Exception e) {
            log.error("Erro ao gerar pdf.", e);
            throw new DataProviderException("Erro ao gerar PDF", e.getCause());
        }
    }

    @Override
    public String salvarLogo(String idUsuario, MultipartFile multipartFile) {
        try {
            Path userDir = Paths.get(BASE_PATH, idUsuario);
            if (!Files.exists(userDir)) {
                Files.createDirectories(userDir);
            }

            String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
            String filename = originalFilename.endsWith(".png")
                    ? originalFilename
                    : originalFilename + ".png";

            Path targetPath = userDir.resolve(filename);

            multipartFile.transferTo(targetPath.toFile());

            return userDir.resolve(filename).toString();
        } catch (Exception ex) {
            log.error("Erro ao salvar logo", ex);
            throw new DataProviderException("Erro ao salvar logo", ex.getCause());
        }
    }

}
