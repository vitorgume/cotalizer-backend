package com.gumeinteligenciacomercial.orcaja.infrastructure.dataprovider;

import com.gumeinteligenciacomercial.orcaja.infrastructure.exceptions.DataProviderException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.http.AbortableInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.awscore.exception.AwsErrorDetails;
import software.amazon.awssdk.core.exception.SdkClientException;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArquivoDataProviderTest {

    @Mock
    S3Client s3;

    ArquivoDataProvider provider;

    @BeforeEach
    void setUp() throws Exception {
        provider = new ArquivoDataProvider(
                "bucket-x",
                "us-east-1",
                Optional.empty(),
                Optional.of(""),
                null,
                Optional.of(""),
                false,
                "https://pub.example.com"
        );
        setPrivateField(provider, "s3", s3);
    }

    @Test
    void salvarPdfDeveSubirNoS3ERetornarUrlPublica() {
        when(s3.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().eTag("abc").build());

        String out = provider.salvarPdf("ARQ-abc12",
                "<html><body><p>Olá</p></body></html>");

        assertEquals("https://pub.example.com/pdf/ARQ-abc12.pdf", out);

        ArgumentCaptor<PutObjectRequest> reqCap = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(s3).putObject(reqCap.capture(), any(RequestBody.class));

        PutObjectRequest req = reqCap.getValue();
        assertEquals("bucket-x", req.bucket());
        assertEquals("pdf/ARQ-abc12.pdf", req.key());
        assertEquals("application/pdf", req.contentType());
        assertEquals(ObjectCannedACL.PRIVATE, req.acl());
    }

    @Test
    void salvarPdfDeveLancarDataProviderExceptionQuandoPutObjectFalha() {
        when(s3.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenThrow(new RuntimeException("boom"));

        DataProviderException ex = assertThrows(DataProviderException.class, () ->
                provider.salvarPdf("ARQ-x", "<html/>"));

        assertEquals("Erro ao salvar pdf.", ex.getMessage());
        verify(s3).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    void salvarLogoDeveUsarExtDoArquivoEContentTypeInformado() throws Exception {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("logo.jpg");
        when(file.getContentType()).thenReturn("image/jpeg");
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[]{1,2,3}));
        when(file.getSize()).thenReturn(3L);

        when(s3.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build());

        String out = provider.salvarLogo("user-1", file);
        assertEquals("https://pub.example.com/tenants/user-1/branding/logo.jpg", out);

        ArgumentCaptor<PutObjectRequest> cap = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(s3).putObject(cap.capture(), any(RequestBody.class));

        PutObjectRequest req = cap.getValue();
        assertEquals("bucket-x", req.bucket());
        assertEquals("tenants/user-1/branding/logo.jpg", req.key());
        assertEquals("image/jpeg", req.contentType());
        assertEquals(ObjectCannedACL.PRIVATE, req.acl());
    }

    @Test
    void salvarLogoDeveAssumirPngQuandoExtDesconhecidaOuNulaEDefinirContentTypeImagePng() throws Exception {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn(null);
        when(file.getContentType()).thenReturn(null);
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[0]));
        when(file.getSize()).thenReturn(0L);

        when(s3.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build());

        String out = provider.salvarLogo("u2", file);
        assertEquals("https://pub.example.com/tenants/u2/branding/logo.png", out);

        ArgumentCaptor<PutObjectRequest> cap = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(s3).putObject(cap.capture(), any(RequestBody.class));

        PutObjectRequest req = cap.getValue();
        assertEquals("tenants/u2/branding/logo.png", req.key());
        assertEquals("image/png", req.contentType());
    }

    @Test
    void salvarLogoDeveLancarDataProviderExceptionQuandoPutObjectFalha() throws Exception {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("logo.webp");
        when(file.getContentType()).thenReturn(null);
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[1]));
        when(file.getSize()).thenReturn(1L);

        when(s3.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenThrow(new RuntimeException("falha-logo"));

        DataProviderException ex = assertThrows(DataProviderException.class,
                () -> provider.salvarLogo("u3", file));
        assertEquals("Erro ao salvar logo", ex.getMessage());

        verify(s3).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    void carregarArquivoDeveRetornarResourceComFilenameContentLengthEDescription() throws IOException {
        String key = "dir/file.dat";

        GetObjectResponse resp = GetObjectResponse.builder()
                .contentLength(42L)
                .build();
        ResponseInputStream<GetObjectResponse> ris = new ResponseInputStream<>(
                resp,
                AbortableInputStream.create(new ByteArrayInputStream(new byte[]{1,2,3}))
        );

        when(s3.getObject(any(GetObjectRequest.class))).thenReturn(ris);

        Resource r = provider.carregarArquivo(key);
        assertNotNull(r);
        assertEquals("file.dat", r.getFilename());
        assertEquals(42L, r.contentLength());
        assertEquals("S3 bucket-x/dir/file.dat", r.getDescription());

        ArgumentCaptor<GetObjectRequest> cap = ArgumentCaptor.forClass(GetObjectRequest.class);
        verify(s3).getObject(cap.capture());
        assertEquals("bucket-x", cap.getValue().bucket());
        assertEquals(key, cap.getValue().key());
    }

    @Test
    void carregarArquivoDeveLancarDataProviderExceptionQuandoGetObjectFalha() {
        when(s3.getObject(any(GetObjectRequest.class)))
                .thenThrow(new RuntimeException("falha-get"));

        DataProviderException ex = assertThrows(DataProviderException.class,
                () -> provider.carregarArquivo("x/y/z.pdf"));
        assertEquals("Erro ao carregar arquivo.", ex.getMessage());

        verify(s3).getObject(any(GetObjectRequest.class));
    }

    @Test
    void deletarArquivo_quandoKeyValida_deveChamarS3ComPrefixoPdf() {
        // given
        when(s3.deleteObject(any(DeleteObjectRequest.class)))
                .thenReturn(DeleteObjectResponse.builder().build());

        // when
        provider.deletarArquivo("ARQ-1.pdf");

        // then
        ArgumentCaptor<DeleteObjectRequest> cap = ArgumentCaptor.forClass(DeleteObjectRequest.class);
        verify(s3).deleteObject(cap.capture());
        DeleteObjectRequest req = cap.getValue();
        assertEquals("bucket-x", req.bucket());
        assertEquals("pdf/ARQ-1.pdf", req.key());
    }

    @Test
    void deletarArquivo_quandoNomeEmBranco_naoChamaS3() {
        provider.deletarArquivo("   "); // normalizeKey -> blank
        verify(s3, never()).deleteObject(any(DeleteObjectRequest.class));
    }

    @Test
    void deletarArquivo_quandoS3Retorna404_naoLancaExcecao() {
        S3Exception notFound = (S3Exception) S3Exception.builder()
                .statusCode(404)
                .awsErrorDetails(AwsErrorDetails.builder().errorCode("NotFound").build())
                .build();
        when(s3.deleteObject(any(DeleteObjectRequest.class))).thenThrow(notFound);

        // não deve lançar
        assertDoesNotThrow(() -> provider.deletarArquivo("ARQ-2.pdf"));
        verify(s3).deleteObject(any(DeleteObjectRequest.class));
    }

    @Test
    void deletarArquivo_quandoErroCodeNoSuchKey_naoLancaExcecao() {
        S3Exception noSuchKey = (S3Exception) S3Exception.builder()
                .statusCode(400) // qualquer que não seja 404
                .awsErrorDetails(AwsErrorDetails.builder().errorCode("NoSuchKey").build())
                .build();
        when(s3.deleteObject(any(DeleteObjectRequest.class))).thenThrow(noSuchKey);

        // não deve lançar
        assertDoesNotThrow(() -> provider.deletarArquivo("ARQ-missing.pdf"));
        verify(s3).deleteObject(any(DeleteObjectRequest.class));
    }

    @Test
    void deletarArquivo_quandoS3ExceptionDiferente_lancaDataProviderException() {
        S3Exception err = (S3Exception) S3Exception.builder()
                .statusCode(500)
                .awsErrorDetails(AwsErrorDetails.builder().errorCode("InternalError").build())
                .build();
        when(s3.deleteObject(any(DeleteObjectRequest.class))).thenThrow(err);

        DataProviderException ex = assertThrows(DataProviderException.class,
                () -> provider.deletarArquivo("ARQ-err.pdf"));
        assertEquals("Erro ao deletar arquivo no S3.", ex.getMessage());
        verify(s3).deleteObject(any(DeleteObjectRequest.class));
    }

    @Test
    void deletarArquivo_quandoFalhaClienteSdk_lancaDataProviderException() {
        when(s3.deleteObject(any(DeleteObjectRequest.class)))
                .thenThrow(SdkClientException.create("network", null));

        DataProviderException ex = assertThrows(DataProviderException.class,
                () -> provider.deletarArquivo("ARQ-timeout.pdf"));
        assertEquals("Erro ao deletar arquivo no S3.", ex.getMessage());
        verify(s3).deleteObject(any(DeleteObjectRequest.class));
    }

    @Test
    void deletarLogo_quandoBlank_naoChamaNada() {
        provider.deletarLogo("  ");
        verify(s3, never()).deleteObject(any(DeleteObjectRequest.class));
    }

    @Test
    void deletarLogo_quandoTemExtensao_deveDelegarParaDeletarArquivo_eRemoverEmPdfFolder() {
        when(s3.deleteObject(any(DeleteObjectRequest.class)))
                .thenReturn(DeleteObjectResponse.builder().build());

        provider.deletarLogo("logo.png");

        ArgumentCaptor<DeleteObjectRequest> cap = ArgumentCaptor.forClass(DeleteObjectRequest.class);
        verify(s3).deleteObject(cap.capture());
        DeleteObjectRequest req = cap.getValue();
        assertEquals("bucket-x", req.bucket());
        assertEquals("pdf/logo.png", req.key());
    }

    @Test
    void deletarLogo_quandoSemExtensao_naoDeveChamarDeletarArquivo() throws Exception {
        ArquivoDataProvider local = new ArquivoDataProvider(
                "bucket-x",
                "us-east-1",
                Optional.empty(),
                Optional.of(""),
                null,
                false,
                "https://pub.example.com"
        );
        setPrivateField(local, "s3", s3);

        ArquivoDataProvider spyProvider = spy(local);

        when(s3.listObjectsV2(any(ListObjectsV2Request.class)))
                .thenReturn(ListObjectsV2Response.builder()
                        .isTruncated(false)
                        .contents(java.util.Collections.emptyList())
                        .build());

        assertDoesNotThrow(() -> spyProvider.deletarLogo("logo"));

        verify(spyProvider, never()).deletarArquivo(anyString());
        verify(s3).listObjectsV2(any(ListObjectsV2Request.class));
        verify(s3, never()).deleteObject(any(DeleteObjectRequest.class));
    }

    @Test
    void deletarArquivo_quandoComecaComBarra_normalizaAntes() {
        when(s3.deleteObject(any(DeleteObjectRequest.class)))
                .thenReturn(DeleteObjectResponse.builder().build());

        provider.deletarArquivo("/ARQ-5.pdf");

        ArgumentCaptor<DeleteObjectRequest> cap = ArgumentCaptor.forClass(DeleteObjectRequest.class);
        verify(s3).deleteObject(cap.capture());
        assertEquals("bucket-x", cap.getValue().bucket());
        assertEquals("pdf/ARQ-5.pdf", cap.getValue().key());
    }

    @Test
    void deletarArquivo_quandoPassaUrlPublica_normalizaRemovendoBase() {
        when(s3.deleteObject(any(DeleteObjectRequest.class)))
                .thenReturn(DeleteObjectResponse.builder().build());

        provider.deletarArquivo("https://pub.example.com/pdf/ARQ-3.pdf");

        ArgumentCaptor<DeleteObjectRequest> cap = ArgumentCaptor.forClass(DeleteObjectRequest.class);
        verify(s3).deleteObject(cap.capture());
        assertEquals("pdf/pdf/ARQ-3.pdf", cap.getValue().key());
    }

    @Test
    void deletarArquivo_quandoPassaUrlDeAcesso_normalizaTrechoArquivosAcessar() {
        when(s3.deleteObject(any(DeleteObjectRequest.class)))
                .thenReturn(DeleteObjectResponse.builder().build());

        provider.deletarArquivo("https://qualquer.host/arquivos/acessar/tenants/u1/branding/logo.png");

        ArgumentCaptor<DeleteObjectRequest> cap = ArgumentCaptor.forClass(DeleteObjectRequest.class);
        verify(s3).deleteObject(cap.capture());
        assertEquals("pdf/tenants/u1/branding/logo.png", cap.getValue().key());
    }

    @Test
    void deletarLogo_quandoPrefixoTemPaginas_multipasChamadasDeleteObjects() throws Exception {
        ArquivoDataProvider local = new ArquivoDataProvider(
                "bucket-x","us-east-1", Optional.empty(), Optional.of(""), null, false, "https://pub.example.com");
        setPrivateField(local, "s3", s3);

        ListObjectsV2Response page1 = ListObjectsV2Response.builder()
                .isTruncated(true)
                .nextContinuationToken("tok-2")
                .contents(List.of(
                        S3Object.builder().key("tenants/u/branding/logo.png").build(),
                        S3Object.builder().key("tenants/u/branding/old.svg").build()
                ))
                .build();

        ListObjectsV2Response page2 = ListObjectsV2Response.builder()
                .isTruncated(false)
                .contents(List.of(
                        S3Object.builder().key("tenants/u/branding/legacy.jpg").build()
                ))
                .build();

        when(s3.listObjectsV2(any(ListObjectsV2Request.class)))
                .thenReturn(page1)
                .thenReturn(page2);

        when(s3.deleteObjects(any(DeleteObjectsRequest.class)))
                .thenReturn(DeleteObjectsResponse.builder().build());

        local.deletarLogo("tenants/u/branding/logo");

        ArgumentCaptor<DeleteObjectsRequest> delCap = ArgumentCaptor.forClass(DeleteObjectsRequest.class);
        verify(s3, times(2)).deleteObjects(delCap.capture());
        List<DeleteObjectsRequest> reqs = delCap.getAllValues();

        assertEquals(2, reqs.get(0).delete().objects().size());

        assertEquals(1, reqs.get(1).delete().objects().size());

        verify(s3, times(2)).listObjectsV2(any(ListObjectsV2Request.class));
    }

    @ParameterizedTest
    @CsvSource({
            "https://pub.example.com/pdf/ARQ-1.pdf, pdf/ARQ-1.pdf",
            "https://site/arquivos/acessar/pdf/ARQ-2.pdf, pdf/ARQ-2.pdf",
            "  /tenants/u/branding/logo.png , tenants/u/branding/logo.png",
            "plain/key, plain/key"
    })
    void normalizeKey_variosCaminhos(String in, String expected) throws Exception {
        ArquivoDataProvider p = new ArquivoDataProvider("bucket-x","us-east-1", Optional.empty(), Optional.of(""), null, false, "https://pub.example.com");
        setPrivateField(p, "s3", s3);
        var m = ArquivoDataProvider.class.getDeclaredMethod("normalizeKey", String.class);
        m.setAccessible(true);
        String out = (String) m.invoke(p, in);
        assertEquals(expected, out);
    }

    @ParameterizedTest
    @CsvSource({
            "a/b/c.png, true",
            "a/b/.hidden, true",
            "a/b/c, false",
            "folder.only/, false",
            "c.jpg, true"
    })
    void hasExtension_casos(String key, boolean expected) throws Exception {
        ArquivoDataProvider p = new ArquivoDataProvider("bucket-x","us-east-1", Optional.empty(), Optional.of(""), null, false, "https://pub.example.com");
        var m = ArquivoDataProvider.class.getDeclaredMethod("hasExtension", String.class);
        m.setAccessible(true);
        boolean out = (boolean) m.invoke(p, key);
        assertEquals(expected, out);
    }

    private static void setPrivateField(Object target, String fieldName, Object value) throws Exception {
        Field f = target.getClass().getDeclaredField(fieldName);
        f.setAccessible(true);
        f.set(target, value);
    }
}