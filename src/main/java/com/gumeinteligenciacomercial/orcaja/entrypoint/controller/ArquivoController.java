package com.gumeinteligenciacomercial.orcaja.entrypoint.controller;

import com.gumeinteligenciacomercial.orcaja.application.usecase.ArquivoUseCase;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.OrcamentoDto;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.OrcamentoTradicionalDto;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.ResponseDto;
import com.gumeinteligenciacomercial.orcaja.entrypoint.mapper.OrcamentoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

    @PostMapping
    public ResponseEntity<ResponseDto<OrcamentoTradicionalDto>> gerarArquivoOrcamentoTradicional(@RequestBody OrcamentoTradicionalDto orcamentoTradiciona) {
        OrcamentoTradicionalDto resultado = OrcamentoMapper.paraDto(useCase.)
    }

    @GetMapping("/acessar/{nomeArquivo}")
    public ResponseEntity<Resource> acessarArquivo(@PathVariable String nomeArquivo) {
        try {
            Path arquivoPath = Paths.get("C:/Users/vitor/orcaja").resolve(nomeArquivo);
            Resource resource = new UrlResource(arquivoPath.toUri());

            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + resource.getFilename())
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/download/{nomeArquivo}")
    public ResponseEntity<Resource> downloadArquivo(@PathVariable String nomeArquivo) {
        try {
            Path arquivoPath = Paths.get("C:/Users/vitor/orcaja").resolve(nomeArquivo);
            Resource resource = new UrlResource(arquivoPath.toUri());

            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + resource.getFilename())
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
