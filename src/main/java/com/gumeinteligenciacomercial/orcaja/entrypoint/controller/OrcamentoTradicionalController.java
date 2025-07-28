package com.gumeinteligenciacomercial.orcaja.entrypoint.controller;

import com.gumeinteligenciacomercial.orcaja.application.usecase.OrcamentoTradicionalUseCase;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.OrcamentoTradicionalDto;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.ResponseDto;
import com.gumeinteligenciacomercial.orcaja.entrypoint.mapper.OrcamentoTradicionalMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/orcamentos/tradicionais")
@RequiredArgsConstructor
public class OrcamentoTradicionalController {

    private final OrcamentoTradicionalUseCase useCase;

    @PostMapping
    public ResponseEntity<ResponseDto<OrcamentoTradicionalDto>> criar(@RequestBody OrcamentoTradicionalDto novoOrcamentoTradicional) {
        OrcamentoTradicionalDto resultado = OrcamentoTradicionalMapper.paraDto(useCase.cadastrar(OrcamentoTradicionalMapper.paraDomain(novoOrcamentoTradicional)));
        ResponseDto<OrcamentoTradicionalDto> response = new ResponseDto<>(resultado);
        return ResponseEntity.created(UriComponentsBuilder
                .newInstance()
                .path("/orcamentos/tradicionais/{id}")
                .buildAndExpand(resultado.getId())
                .toUri()
        ).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDto<OrcamentoTradicionalDto>> consultarPorId(@PathVariable("id") String id) {
        OrcamentoTradicionalDto resultado = OrcamentoTradicionalMapper.paraDto(useCase.consultarPorId(id));
        ResponseDto<OrcamentoTradicionalDto> response = new ResponseDto<>(resultado);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/usuario/{id}")
    public ResponseEntity<ResponseDto<Page<OrcamentoTradicionalDto>>> listarPorUsuario(@PathVariable("id") String idUsuario, @PageableDefault Pageable pageable) {
        Page<OrcamentoTradicionalDto> resultado = OrcamentoTradicionalMapper.paraDtos(useCase.listarPorUsuario(idUsuario, pageable));
        ResponseDto<Page<OrcamentoTradicionalDto>> response = new ResponseDto<>(resultado);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDto<OrcamentoTradicionalDto>> alterar(@RequestBody OrcamentoTradicionalDto novosDados, @PathVariable("id") String id) {
        OrcamentoTradicionalDto resultado = OrcamentoTradicionalMapper.paraDto(useCase.alterar(id, OrcamentoTradicionalMapper.paraDomain(novosDados)));
        ResponseDto<OrcamentoTradicionalDto> response = new ResponseDto<>(resultado);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable("id") String id) {
        useCase.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
