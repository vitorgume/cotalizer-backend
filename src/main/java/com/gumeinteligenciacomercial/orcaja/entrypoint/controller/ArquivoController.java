package com.gumeinteligenciacomercial.orcaja.entrypoint.controller;

import com.gumeinteligenciacomercial.orcaja.application.usecase.ArquivoUseCase;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.LogoDto;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.OrcamentoDto;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.OrcamentoTradicionalDto;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.ResponseDto;
import com.gumeinteligenciacomercial.orcaja.entrypoint.mapper.OrcamentoMapper;
import com.gumeinteligenciacomercial.orcaja.entrypoint.mapper.OrcamentoTradicionalMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("arquivos")
@RequiredArgsConstructor
public class ArquivoController {

    private final ArquivoUseCase useCase;

    @PostMapping
    public ResponseEntity<ResponseDto<OrcamentoDto>> gerarArquivoOrcamento(@RequestBody OrcamentoDto orcamento) {
        OrcamentoDto resultado = OrcamentoMapper.paraDto(useCase.salvarArquivo(OrcamentoMapper.paraDomain(orcamento)));
        ResponseDto<OrcamentoDto> response = new ResponseDto<>(resultado);
        return ResponseEntity.created(UriComponentsBuilder
                .newInstance()
                .path("/arquivos/{id}")
                .buildAndExpand(resultado.getId())
                .toUri()
        ).body(response);
    }

    @PostMapping("tradicional")
    public ResponseEntity<ResponseDto<OrcamentoTradicionalDto>> gerarArquivoOrcamentoTradicional(@RequestBody OrcamentoTradicionalDto orcamentoTradicional) {
        OrcamentoTradicionalDto resultado = OrcamentoTradicionalMapper.paraDto(useCase.salvarArquivoTradicional(OrcamentoTradicionalMapper.paraDomain(orcamentoTradicional)));
        ResponseDto<OrcamentoTradicionalDto> response = new ResponseDto<>(resultado);
        return ResponseEntity.created(UriComponentsBuilder
                .newInstance()
                .path("/arquivos/tradicional/{id}")
                .buildAndExpand(resultado.getId())
                .toUri()
        ).body(response);
    }

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

    @PostMapping("/logo")
    public ResponseEntity<ResponseDto<LogoDto>> cadastrar(@RequestParam("logo") MultipartFile multipartFile, @RequestParam("idUsuario") String idUsuario) {
        LogoDto resultado = LogoDto.builder().idUsuario(idUsuario).urlFoto(useCase.cadastrarLogo(idUsuario, multipartFile)).build();
        ResponseDto<LogoDto> response = new ResponseDto<>(resultado);

        return ResponseEntity.created(
                UriComponentsBuilder
                        .newInstance()
                        .path("/logos/{id}")
                        .buildAndExpand(resultado.getIdUsuario())
                        .toUri()
        ).body(response);
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
