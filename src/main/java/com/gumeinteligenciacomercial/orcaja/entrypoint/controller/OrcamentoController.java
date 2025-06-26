package com.gumeinteligenciacomercial.orcaja.entrypoint.controller;

import com.gumeinteligenciacomercial.orcaja.application.usecase.OrcamentoUseCase;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.OrcamentoDto;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.ResponseDto;
import com.gumeinteligenciacomercial.orcaja.entrypoint.mapper.OrcamentoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@RequestMapping("orcamentos")
@RequiredArgsConstructor
public class OrcamentoController {

    private final OrcamentoUseCase useCase;

    @PostMapping
    public ResponseEntity<ResponseDto<OrcamentoDto>> gerar(@RequestBody OrcamentoDto novoOrcamento) {
        OrcamentoDto resultado = OrcamentoMapper.paraDto(useCase.cadastrar(OrcamentoMapper.paraDomain(novoOrcamento)));
        ResponseDto<OrcamentoDto> response = new ResponseDto<>(resultado);
        return ResponseEntity.created(
                UriComponentsBuilder
                        .newInstance()
                        .path("/orcamentos/{id}")
                        .buildAndExpand(resultado.getId())
                        .toUri())
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDto<OrcamentoDto>> consultarPorId(@PathVariable("id") String  idOrcamento) {
        OrcamentoDto resultado = OrcamentoMapper.paraDto(useCase.consultarPorId(idOrcamento));
        ResponseDto<OrcamentoDto> response = new ResponseDto<>(resultado);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/usuario/{id}")
    public ResponseEntity<ResponseDto<Page<OrcamentoDto>>> listarPorUsuario(@PathVariable("id") String  idUsuario, @PageableDefault Pageable pageable) {
        Page<OrcamentoDto> resultado = OrcamentoMapper.paraDtos(useCase.listarPorUsuario(idUsuario, pageable));
        ResponseDto<Page<OrcamentoDto>> response = new ResponseDto<>(resultado);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable("id") String  id) {
        useCase.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDto<OrcamentoDto>> alterar(@PathVariable("id") String idOrcamento, @RequestBody OrcamentoDto novosDados) {
        OrcamentoDto resultado = OrcamentoMapper.paraDto(useCase.alterar(idOrcamento, OrcamentoMapper.paraDomain(novosDados)));
        ResponseDto<OrcamentoDto> response = new ResponseDto<>(resultado);

        return ResponseEntity.ok(response);
    }
}
