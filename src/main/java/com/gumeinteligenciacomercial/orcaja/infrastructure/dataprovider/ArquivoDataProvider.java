package com.gumeinteligenciacomercial.orcaja.infrastructure.dataprovider;

import com.gumeinteligenciacomercial.orcaja.application.gateway.ArquivoGateway;
import com.gumeinteligenciacomercial.orcaja.infrastructure.exceptions.DataProviderException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.*;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
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
            @Value("${app.storage.s3.access-key:}") Optional<String> accessKey,
            @Value("${app.storage.s3.secret-key:}") Optional<String> secretKey,
            @Value("${app.storage.s3.session-token:}") Optional<String> sessionToken,
            @Value("${app.storage.s3.path-style:false}") boolean pathStyle,
            @Value("${app.files.public-base-url}") String publicBaseUrl
    ) {
        // Config S3: sem endpointOverride em produção
        var s3Cfg = S3Configuration.builder()
                .pathStyleAccessEnabled(pathStyle) // true se bucket tiver "." no nome
                .build();

        // Credenciais
        AwsCredentialsProvider credsProvider;
        boolean hasAccess = accessKey.filter(k -> !k.isBlank()).isPresent();
        boolean hasSecret = secretKey.filter(s -> !s.isBlank()).isPresent();

        if (hasAccess || hasSecret) {
            if (!(hasAccess && hasSecret)) {
                throw new IllegalStateException("Defina access-key e secret-key ou nenhuma das duas.");
            }
            credsProvider = sessionToken.filter(t -> !t.isBlank()).isPresent()
                    ? StaticCredentialsProvider.create(
                    AwsSessionCredentials.create(accessKey.get(), secretKey.get(), sessionToken.get()))
                    : StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(accessKey.get(), secretKey.get()));
        } else {
            // AWS real: usa env/profile/role (DefaultCredentialsProvider)
            credsProvider = DefaultCredentialsProvider.create();
        }

        this.s3 = S3Client.builder()
                .region(Region.of(region))
                .serviceConfiguration(s3Cfg)
                .credentialsProvider(credsProvider)
                .build();

        this.presigner = S3Presigner.builder()
                .region(Region.of(region))
                .credentialsProvider(credsProvider)
                .build();

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
                @Override public long contentLength() { return len; }
                @Override public String getFilename() {
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

    @Override
    public void deletarArquivo(String nomeArquivo) {
        String key = normalizeKey(nomeArquivo);
        if (key.isBlank()) return;

        try {
            s3.deleteObject(DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key("pdf/" + key)
                    .build());
            log.info("Objeto removido do S3: {}/{}", bucket, key);
        } catch (S3Exception e) {
            if (e.statusCode() == 404 || "NoSuchKey".equalsIgnoreCase(e.awsErrorDetails().errorCode())) {
                log.warn("Objeto não encontrado para remover: {}/{}", bucket, key);
                return;
            }
            log.error("Erro ao deletar objeto S3: {}/{}", bucket, key, e);
            throw new DataProviderException("Erro ao deletar arquivo no S3.", e);
        } catch (SdkClientException | AwsServiceException e) {
            log.error("Falha cliente/AWS ao deletar objeto: {}/{}", bucket, key, e);
            throw new DataProviderException("Erro ao deletar arquivo no S3.", e);
        }
    }

    @Override
    public void deletarLogo(String nomeLogo) {
        String keyOrPrefix = normalizeKey(nomeLogo);
        if (keyOrPrefix.isBlank()) return;

        if (!hasExtension(keyOrPrefix)) {
            deleteByPrefix(keyOrPrefix);
        } else {
            deletarArquivo(keyOrPrefix);
        }
    }

    /* ===================== helpers ===================== */

    private String normalizeKey(String nomeOuUrl) {
        if (nomeOuUrl == null) return "";
        String n = nomeOuUrl.trim();

        String pub = publicBaseUrl;
        if (n.startsWith(pub)) {
            n = n.substring(pub.length());
        } else if (n.startsWith("http://") || n.startsWith("https://")) {
            int i = n.indexOf("/arquivos/acessar/");
            if (i >= 0) n = n.substring(i + "/arquivos/acessar/".length());
        }

        if (n.startsWith("/")) n = n.substring(1);
        return n;
    }

    private boolean hasExtension(String key) {
        int slash = key.lastIndexOf('/');
        int dot = key.lastIndexOf('.');
        return dot > slash;
    }

    private void deleteByPrefix(String prefix) {
        String p = prefix;

        if (p.startsWith("/")) p = p.substring(1);

        String token = null;
        do {
            var listReq = ListObjectsV2Request.builder()
                    .bucket(bucket)
                    .prefix(p)
                    .continuationToken(token)
                    .build();

            var listResp = s3.listObjectsV2(listReq);
            var contents = listResp.contents();

            if (contents == null || contents.isEmpty()) {
                log.info("Nenhum objeto com prefixo '{}' para remover.", p);
                break;
            }

            List<ObjectIdentifier> toDelete = new ArrayList<>(contents.size());
            for (var obj : contents) {
                toDelete.add(ObjectIdentifier.builder().key(obj.key()).build());
            }

            var delReq = DeleteObjectsRequest.builder()
                    .bucket(bucket)
                    .delete(Delete.builder().objects(toDelete).build())
                    .build();

            s3.deleteObjects(delReq);
            log.info("Removidos {} objetos com prefixo '{}' no bucket {}.", toDelete.size(), p, bucket);

            token = listResp.isTruncated() ? listResp.nextContinuationToken() : null;
        } while (token != null);
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
