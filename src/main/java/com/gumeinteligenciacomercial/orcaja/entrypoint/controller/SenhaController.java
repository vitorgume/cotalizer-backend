package com.gumeinteligenciacomercial.orcaja.entrypoint.controller;

import com.gumeinteligenciacomercial.orcaja.application.usecase.SenhaUseCase;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.SolicitacaoNovaSenhaDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("senhas")
@RequiredArgsConstructor
public class SenhaController {

    private final SenhaUseCase senhaUseCase;

    @PostMapping("solicitar/nova")
    public ResponseEntity<Void> solicitarNovaSenha(@RequestBody SolicitacaoNovaSenhaDto novaSolicitacao) {
        senhaUseCase.solicitarNovaSenha(novaSolicitacao.getIdUsuario());
        return ResponseEntity.ok().build();
    }

}

