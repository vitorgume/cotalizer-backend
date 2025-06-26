package com.gumeinteligenciacomercial.orcaja.entrypoint.controller;

import com.gumeinteligenciacomercial.orcaja.application.usecase.LoginUseCase;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.LoginDto;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.ResponseDto;
import com.gumeinteligenciacomercial.orcaja.entrypoint.mapper.LoginMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("login")
@RequiredArgsConstructor
public class LoginController {

    private final LoginUseCase loginUseCase;

    @PostMapping
    public ResponseEntity<ResponseDto<LoginDto>> logar(@RequestBody LoginDto loginDto) {
        LoginDto resultado = LoginMapper.paraDto(loginUseCase.autenticar(LoginMapper.paraDomain(loginDto)));
        ResponseDto<LoginDto> response = new ResponseDto<>(resultado);
        return ResponseEntity.ok(response);
    }

}
