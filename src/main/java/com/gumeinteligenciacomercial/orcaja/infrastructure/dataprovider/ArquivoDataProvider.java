package com.gumeinteligenciacomercial.orcaja.infrastructure.dataprovider;

import com.gumeinteligenciacomercial.orcaja.application.gateway.ArquivoGateway;
import com.gumeinteligenciacomercial.orcaja.infrastructure.exceptions.DataProviderException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;
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
            @Value("${app.storage.s3.endpoint}") Optional<String> endpoint,
            @Value("${app.storage.s3.access-key}") Optional<String> accessKey,
            @Value("${app.storage.s3.secret-key}") Optional<String> secretKey,
            @Value("${app.storage.s3.session-token:}") Optional<String> sessionToken,
            @Value("${app.files.public-base-url}") String publicBaseUrl
    ) {
        var s3Cfg = S3Configuration.builder()
                // path-style só quando usa endpoint S3-compatível (MinIO, etc.)
                .pathStyleAccessEnabled(endpoint.filter(e -> !e.isBlank()).isPresent())
                .build();

        // Definição de credenciais
        AwsCredentialsProvider credsProvider;
        boolean hasStaticCreds = accessKey.filter(k -> !k.isBlank()).isPresent()
                && secretKey.filter(s -> !s.isBlank()).isPresent();

        if (hasStaticCreds) {
            var basic = sessionToken.filter(t -> !t.isBlank()).isPresent()
                    ? software.amazon.awssdk.auth.credentials.AwsSessionCredentials.create(
                    accessKey.get(), secretKey.get(), sessionToken.get())
                    : AwsBasicCredentials.create(accessKey.get(), secretKey.get());
            credsProvider = StaticCredentialsProvider.create(basic);
        } else if (endpoint.filter(e -> !e.isBlank()).isPresent()) {
            // endpoint custom (MinIO) quase sempre precisa credenciais explícitas
            throw new IllegalStateException("Defina access-key/secret-key para endpoint S3 custom.");
        } else {
            // AWS real: deixa o DefaultCredentialsProvider (ENVs padrão, profile, role/IMDS)
            credsProvider = DefaultCredentialsProvider.create();
        }

        var s3Builder = S3Client.builder()
                .region(Region.of(region))
                .serviceConfiguration(s3Cfg)
                .credentialsProvider(credsProvider);

        var presignerBuilder = S3Presigner.builder()
                .region(Region.of(region))
                .credentialsProvider(credsProvider);

        endpoint.filter(e -> !e.isBlank()).ifPresent(url -> {
            var ep = URI.create(url);
            s3Builder.endpointOverride(ep);
            presignerBuilder.endpointOverride(ep);
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

        try {
            var putReq = software.amazon.awssdk.services.s3.model.PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType("application/pdf")
                    .acl(software.amazon.awssdk.services.s3.model.ObjectCannedACL.PRIVATE)
                    .build();

            s3.putObject(putReq, software.amazon.awssdk.core.sync.RequestBody.fromBytes(pdfBytes));
        } catch (Exception ex) {
            log.error("Erro ao salvar pdf.", ex);
            throw new DataProviderException("Erro ao salvar pdf.", ex.getCause());
        }

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
        } catch (Exception ex) {
            log.error("Erro ao salvar logo", ex);
            throw new DataProviderException("Erro ao salvar logo", ex.getCause());
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
                @Override
                public long contentLength() {
                    return len;
                } // <- evita consumir o stream

                @Override
                public String getFilename() {
                    int i = keyOuNomeArquivo.lastIndexOf('/');
                    return i >= 0 ? keyOuNomeArquivo.substring(i + 1) : keyOuNomeArquivo;
                }

                @Override
                public String getDescription() {
                    return "S3 " + bucket + "/" + keyOuNomeArquivo;
                }
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
        return switch (ext) {
            case "png", "jpg", "jpeg", "svg", "webp" -> ext;
            default -> "png";
        };
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
            log.error("Erro ao gerar PDF", e);
            throw new DataProviderException("Erro ao gerar PDF", e);
        }
    }

}
