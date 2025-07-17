package com.gumeinteligenciacomercial.orcaja.entrypoint.controller;

import com.gumeinteligenciacomercial.orcaja.application.usecase.AssinaturaUseCase;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.*;
import com.gumeinteligenciacomercial.orcaja.entrypoint.mapper.AssinaturaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/assinaturas")
@RequiredArgsConstructor
public class AssinaturaController {

    private final AssinaturaUseCase assinaturaUseCase;

    @PostMapping
    public ResponseEntity<ResponseDto<AssinaturaResponseDto>> pagar(@RequestBody AssinaturaRequestDto novaAssinatura) {
        AssinaturaResponseDto resultado = AssinaturaMapper.paraDto(assinaturaUseCase.criarAssinatura(AssinaturaMapper.paraDomain(novaAssinatura)));
        ResponseDto<AssinaturaResponseDto> response = new ResponseDto<>(resultado);
        return ResponseEntity.ok(response);
    }
}
