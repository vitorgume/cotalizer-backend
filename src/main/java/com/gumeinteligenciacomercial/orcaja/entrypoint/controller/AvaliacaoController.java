package com.gumeinteligenciacomercial.orcaja.entrypoint.controller;

import com.gumeinteligenciacomercial.orcaja.application.usecase.AvaliacaoUseCase;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.AvaliacaoDto;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.ResponseDto;
import com.gumeinteligenciacomercial.orcaja.entrypoint.mapper.AvaliacaoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("avaliacoes")
@RequiredArgsConstructor
public class AvaliacaoController {

    private final AvaliacaoUseCase avaliacaoUseCase;

    @PostMapping
    public ResponseEntity<ResponseDto<AvaliacaoDto>> enviar(@RequestBody AvaliacaoDto novoAvaliacao) {
        AvaliacaoDto resultado = AvaliacaoMapper.paraDto(avaliacaoUseCase.enviar(AvaliacaoMapper.paraDomain(novoAvaliacao)));
        ResponseDto<AvaliacaoDto> response = new ResponseDto<>(resultado);
        return ResponseEntity.created(
                UriComponentsBuilder
                        .newInstance()
                        .path("/avaliacoes/{id}")
                        .buildAndExpand(resultado.getIdUsuario())
                        .toUri()
        ).body(response);
    }
}
