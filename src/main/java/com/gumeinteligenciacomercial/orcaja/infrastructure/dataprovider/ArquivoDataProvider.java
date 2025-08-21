package com.gumeinteligenciacomercial.orcaja.infrastructure.dataprovider;

import com.gumeinteligenciacomercial.orcaja.application.gateway.ArquivoGateway;
import com.gumeinteligenciacomercial.orcaja.infrastructure.exceptions.DataProviderException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.xhtmlrenderer.pdf.ITextRenderer;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.time.Duration;
import java.util.Objects;

@Component
@Slf4j
@RequiredArgsConstructor
public class ArquivoDataProvider implements ArquivoGateway {

    private static final String BASE_PATH = "C:/Users/vitor/orcaja";
    private static final String BASE_API_FILE = "http://localhost:8080/arquivos/acessar/";

    private final S3Client s3;
    private final S3Presigner presigner;

    @Value("${app.s3.bucket}")
    private String bucket;

    @Value("${app.s3.prefix:}")
    private String prefix;

    @Value("${app.s3.presign.ttl-seconds:900}")
    private long presignTtlSeconds;

    @Override
    public String salvarPdf(String nomeArquivo, String html) {
        String safeName = nomeArquivo
                .replaceAll("[^a-zA-Z0-9._-]", "_")
                .concat(".pdf");
        String key = "pdfs/" + safeName;

        String savedKey = this.salvarPdfS3(key, html);

        return this.gerarUrlPresignadaGet(savedKey, false);
    }

    public String salvarPdfS3(String key, String html) {
        byte[] pdf = renderPdf(html);

        PutObjectRequest put = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType("application/pdf")
                .contentDisposition("inline; filename=\"" + Paths.get(key).getFileName().toString() + "\"")
                .cacheControl("no-cache")
                .build();

        s3.putObject(put, RequestBody.fromBytes(pdf));
        return key;
    }

    private byte[] renderPdf(String html) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(html);
            renderer.layout();
            renderer.createPDF(baos);
            return baos.toByteArray();
        } catch (Exception e) {
            log.error("Erro ao renderizar PDF", e);
            throw new RuntimeException("Erro ao renderizar PDF", e);
        }
    }

    /** Gera URL pré-assinada (GET) para visualizar/baixar o PDF. */
    public String gerarUrlPresignadaGet(String key, boolean forcarDownload) {
        GetObjectRequest.Builder get = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key);

        // você pode sobrescrever headers de resposta na URL pré-assinada:
        if (forcarDownload) {
            String fname = Paths.get(key).getFileName().toString();
            get = get.responseContentType("application/pdf")
                    .responseContentDisposition("attachment; filename=\"" + fname + "\"");
        }

        GetObjectPresignRequest pre = GetObjectPresignRequest.builder()
                .getObjectRequest(get.build())
                .signatureDuration(Duration.ofSeconds(presignTtlSeconds)) // <= 7 dias
                .build();

        return presigner.presignGetObject(pre).url().toString();
    }

    @Override
    public String salvarLogo(String idUsuario, MultipartFile multipartFile) {
        try {
            String original = Objects.requireNonNull(multipartFile.getOriginalFilename(), "arquivo sem nome");
            String clean = StringUtils.getFilename(original);

            clean = Normalizer.normalize(clean, Normalizer.Form.NFD)
                    .replaceAll("\\p{M}", "")
                    .replaceAll("[^a-zA-Z0-9._-]", "_");

            String filename = clean.toLowerCase().endsWith(".png") ? clean : (clean + ".png");

            String key = (prefix == null ? "" : prefix) + idUsuario + "/" + filename;

            String contentType = "image/png";

            try (InputStream is = multipartFile.getInputStream()) {
                PutObjectRequest put = PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(key)
                        .contentType(contentType)
                        .cacheControl("public, max-age=31536000, immutable")
                        .build();

                s3.putObject(put, RequestBody.fromInputStream(is, multipartFile.getSize()));
            }

            String regionHint = System.getProperty("aws.region", "");

            return key;
        } catch (Exception ex) {
            log.error("Erro ao salvar logo no S3", ex);
            throw new RuntimeException("Erro ao salvar logo", ex);
        }
    }

}
