package com.gumeinteligenciacomercial.orcaja.entrypoint.controller;

import com.gumeinteligenciacomercial.orcaja.application.usecase.UsuarioUseCase;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.ResponseDto;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.VerificacaoEmailDto;
import com.gumeinteligenciacomercial.orcaja.entrypoint.mapper.VerificacaoEmailMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("verificaoes")
@RequiredArgsConstructor
public class CodigoVerificacaoEmailController {

    private final UsuarioUseCase usuarioUseCase;

    @PostMapping("/email")
    public ResponseEntity<ResponseDto<VerificacaoEmailDto>> verificarEmail(@RequestBody VerificacaoEmailDto verificaoEmail) {
        VerificacaoEmailDto resultado = VerificacaoEmailMapper.paraDto(usuarioUseCase.validarCodigoVerificacao(verificaoEmail.getEmail(), verificaoEmail.getCodigo()));
        ResponseDto<VerificacaoEmailDto> response = new ResponseDto<>(resultado);
        return ResponseEntity.ok(response);
    }
}
