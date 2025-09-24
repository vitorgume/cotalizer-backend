package com.gumeinteligenciacomercial.orcaja.entrypoint.controller;

import com.gumeinteligenciacomercial.orcaja.application.usecase.PlanoUseCase;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.PlanoDto;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.ResponseDto;
import com.gumeinteligenciacomercial.orcaja.entrypoint.mapper.PlanoMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/planos")
@RequiredArgsConstructor
public class PlanoController {

    private final PlanoUseCase planoUseCase;

    @GetMapping
    public ResponseEntity<ResponseDto<List<PlanoDto>>> listar() {
        List<PlanoDto> resultado = planoUseCase.listar().stream().map(PlanoMapper::paraDto).toList();
        ResponseDto<List<PlanoDto>> response = new ResponseDto<>(resultado);
        return ResponseEntity.ok(response);
    }

}
