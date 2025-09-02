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
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api/arquivos")
@RequiredArgsConstructor
public class ArquivoApiController {

    private final ArquivoUseCase useCase;

    @PostMapping
    public ResponseEntity<ResponseDto<OrcamentoDto>> gerarArquivoOrcamento(@RequestBody OrcamentoDto orcamento) {
        OrcamentoDto resultado = OrcamentoMapper.paraDto(useCase.salvarArquivo(OrcamentoMapper.paraDomain(orcamento)));
        ResponseDto<OrcamentoDto> response = new ResponseDto<>(resultado);
        return ResponseEntity.created(UriComponentsBuilder
                .newInstance()
                .path("/api/arquivos/{id}")
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
                .path("/api/arquivos/tradicional/{id}")
                .buildAndExpand(resultado.getId())
                .toUri()
        ).body(response);
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

    @DeleteMapping("/arquivo/{*nomeArquivo}")
    public ResponseEntity<Void> deletarArquivo(@PathVariable("nomeArquivo")  String nomeArquivo) {
        useCase.deletaArquivo(nomeArquivo);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/logo/{*nomeLogo}")
    public ResponseEntity<Void> deletarLogo(@PathVariable("nomeLogo") String nomeLogo) {
        useCase.deletarLogo(nomeLogo);
        return ResponseEntity.noContent().build();
    }
}
