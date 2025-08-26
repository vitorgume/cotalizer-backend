package com.gumeinteligenciacomercial.orcaja.infrastructure.dataprovider;

import com.gumeinteligenciacomercial.orcaja.application.gateway.ArquivoGateway;
import com.gumeinteligenciacomercial.orcaja.infrastructure.exceptions.DataProviderException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.util.Optional;

@Component
@Slf4j
public class ArquivoDataProvider implements ArquivoGateway {

    private final S3Client s3;
    private final S3Presigner presigner;
    private final String bucket;
    private final String publicBaseUrl;

    public ArquivoDataProvider(
            @Value("${app.storage.s3.bucket}") String bucket,
            @Value("${app.storage.s3.region}") String region,
            @Value("${app.storage.s3.endpoint:}") Optional<String> endpoint,
            @Value("${app.files.public-base-url}") String publicBaseUrl
    ) {
        var cfg = software.amazon.awssdk.services.s3.S3Configuration.builder()
                .pathStyleAccessEnabled(true)
                .build();

        var s3Builder = software.amazon.awssdk.services.s3.S3Client.builder()
                .region(software.amazon.awssdk.regions.Region.of(region))
                .serviceConfiguration(cfg);

        var presignerBuilder = software.amazon.awssdk.services.s3.presigner.S3Presigner.builder()
                .region(software.amazon.awssdk.regions.Region.of(region));


        endpoint.filter(e -> !e.isBlank()).ifPresent(url -> {
            var ep = java.net.URI.create(url);
            s3Builder.endpointOverride(ep)
                    .credentialsProvider(software.amazon.awssdk.auth.credentials.StaticCredentialsProvider.create(
                            software.amazon.awssdk.auth.credentials.AwsBasicCredentials.create("test", "test")));
            presignerBuilder.endpointOverride(ep)
                    .credentialsProvider(software.amazon.awssdk.auth.credentials.StaticCredentialsProvider.create(
                            software.amazon.awssdk.auth.credentials.AwsBasicCredentials.create("test", "test")));
        });

        this.s3 = s3Builder.build();
        this.presigner = presignerBuilder.build();
        this.bucket = bucket;
        this.publicBaseUrl = publicBaseUrl.endsWith("/") ? publicBaseUrl : publicBaseUrl + "/";
    }

    @Override
    public String salvarPdf(String nomeArquivo, String html) {
        String key = "pdf/" + nomeArquivo + ".pdf";
        byte[] pdfBytes = renderHtmlToPdf(html);

        var putReq = software.amazon.awssdk.services.s3.model.PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType("application/pdf")
                .acl(software.amazon.awssdk.services.s3.model.ObjectCannedACL.PRIVATE)
                .build();

        s3.putObject(putReq, software.amazon.awssdk.core.sync.RequestBody.fromBytes(pdfBytes));

        return publicBaseUrl + key;
    }

    @Override
    public String salvarLogo(String idUsuario, MultipartFile multipartFile) {
        String ext = getExtOrPng(multipartFile.getOriginalFilename());
        String key = "tenants/" + idUsuario + "/branding/logo." + ext;

        try (var in = multipartFile.getInputStream()) {
            var putReq = software.amazon.awssdk.services.s3.model.PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(multipartFile.getContentType() != null ? multipartFile.getContentType() : ("image/" + ext))
                    .acl(software.amazon.awssdk.services.s3.model.ObjectCannedACL.PRIVATE)
                    .build();

            s3.putObject(putReq, software.amazon.awssdk.core.sync.RequestBody.fromInputStream(in, multipartFile.getSize()));
        } catch (java.io.IOException e) {
            throw new DataProviderException("Erro ao salvar logo", e);
        }

        return publicBaseUrl + key;
    }

    @Override
    public Resource carregarArquivo(String keyOuNomeArquivo) {

        try {
            var getReq = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(keyOuNomeArquivo)
                    .build();

            ResponseInputStream<GetObjectResponse> in = s3.getObject(getReq);

            final long len = in.response().contentLength() != null ? in.response().contentLength() : -1L;

            return new InputStreamResource(in) {
                @Override public long contentLength() { return len; } // <- evita consumir o stream
                @Override public String getFilename() {
                    int i = keyOuNomeArquivo.lastIndexOf('/');
                    return i >= 0 ? keyOuNomeArquivo.substring(i + 1) : keyOuNomeArquivo;
                }
                @Override public String getDescription() { return "S3 " + bucket + "/" + keyOuNomeArquivo; }
            };
        } catch (Exception ex) {
            log.error("Erro ao carregar arquivo.", ex);
            throw new DataProviderException("Erro ao carregar arquivo.", ex.getCause());
        }

    }

    private static String getExtOrPng(String filename) {
        if (filename == null) return "png";
        int i = filename.lastIndexOf('.');
        if (i < 0) return "png";
        String ext = filename.substring(i + 1).toLowerCase();
        return switch (ext) { case "png","jpg","jpeg","svg","webp" -> ext; default -> "png"; };
    }

    private static byte[] renderHtmlToPdf(String html) {
        try (var baos = new java.io.ByteArrayOutputStream()) {
            org.xhtmlrenderer.pdf.ITextRenderer renderer = new org.xhtmlrenderer.pdf.ITextRenderer();
            renderer.setDocumentFromString(html);
            renderer.layout();
            renderer.createPDF(baos);
            renderer.finishPDF();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new com.gumeinteligenciacomercial.orcaja.infrastructure.exceptions.DataProviderException("Erro ao gerar PDF", e);
        }
    }

}
