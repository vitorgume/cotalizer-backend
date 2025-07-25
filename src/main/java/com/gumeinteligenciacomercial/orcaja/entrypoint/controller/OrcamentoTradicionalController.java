package com.gumeinteligenciacomercial.orcaja.entrypoint.controller;

import com.gumeinteligenciacomercial.orcaja.application.usecase.OrcamentoTradicionalUseCase;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.OrcamentoTradicionalDto;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.ResponseDto;
import com.gumeinteligenciacomercial.orcaja.entrypoint.mapper.OrcamentoTradicionalMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orcamentos/tradicionais")
@RequiredArgsConstructor
public class OrcamentoTradicionalController {

    private final OrcamentoTradicionalUseCase useCase;

    @PostMapping
    public ResponseEntity<ResponseDto<OrcamentoTradicionalDto>> criar(@RequestBody OrcamentoTradicionalDto novoOrcamentoTradicional) {
        OrcamentoTradicionalDto resultado = OrcamentoTradicionalMapper.paraDto(useCase.cadastrar(OrcamentoTradicionalMapper.paraDomain(novoOrcamentoTradicional)));
        ResponseDto<OrcamentoTradicionalDto> response = new ResponseDto<>(resultado);
        return ResponseEntity.ok(response);
    }
}
