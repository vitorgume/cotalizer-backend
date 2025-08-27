package com.gumeinteligenciacomercial.orcaja.entrypoint.controller;

import com.gumeinteligenciacomercial.orcaja.application.usecase.ArquivoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/arquivos")
@RequiredArgsConstructor
public class ArquivoPublicoController {

    private final ArquivoUseCase useCase;

    @GetMapping("/acessar/{*nomeArquivo}")
    public ResponseEntity<Resource> acessarArquivo(@PathVariable("nomeArquivo") String nomeArquivo) {
        Resource resource = useCase.acessarArquivo(nomeArquivo);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.inline().filename(filename(nomeArquivo)).build().toString())
                .contentType(guessContentType(nomeArquivo))
                .body(resource);
    }

    @GetMapping("/download/{*nomeArquivo}")
    public ResponseEntity<Resource> downloadArquivo(@PathVariable("nomeArquivo") String nomeArquivo) {
        Resource resource = useCase.downloadArquivo("pdf" + nomeArquivo);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(filename(nomeArquivo)).build().toString())
                .contentType(guessContentType(nomeArquivo))
                .body(resource);
    }

    private static String filename(String key) {
        int i = key.lastIndexOf('/');
        return i >= 0 ? key.substring(i + 1) : key;
    }

    private static MediaType guessContentType(String key) {
        String lower = key.toLowerCase();
        if (lower.endsWith(".pdf")) return MediaType.APPLICATION_PDF;
        if (lower.endsWith(".png")) return MediaType.IMAGE_PNG;
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return MediaType.IMAGE_JPEG;
        if (lower.endsWith(".svg")) return MediaType.valueOf("image/svg+xml");
        return MediaType.APPLICATION_OCTET_STREAM;
    }
}
