package com.gumeinteligenciacomercial.orcaja.entrypoint.controller;

import com.gumeinteligenciacomercial.orcaja.application.usecase.TemplateUseCase;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.ResponseDto;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.TemplateDto;
import com.gumeinteligenciacomercial.orcaja.entrypoint.mapper.TemplateMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("templates")
@RequiredArgsConstructor
public class TemplateController {

    private final TemplateUseCase useCase;

    @GetMapping
    public ResponseEntity<ResponseDto<List<TemplateDto>>> listarTodos() {
        List<TemplateDto> resultado = useCase.listarTodos().stream().map(TemplateMapper::paraDto).toList();
        ResponseDto<List<TemplateDto>> response = new ResponseDto<>(resultado);
        return ResponseEntity.ok(response);
    }

}
