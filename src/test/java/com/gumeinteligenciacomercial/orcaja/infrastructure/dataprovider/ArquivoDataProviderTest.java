package com.gumeinteligenciacomercial.orcaja.infrastructure.dataprovider;

import com.gumeinteligenciacomercial.orcaja.infrastructure.exceptions.DataProviderException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
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

    private static void setPrivateField(Object target, String fieldName, Object value) throws Exception {
        Field f = target.getClass().getDeclaredField(fieldName);
        f.setAccessible(true);
        f.set(target, value);
    }
}