package com.gumeinteligenciacomercial.orcaja.entrypoint.controller;

import com.gumeinteligenciacomercial.orcaja.application.usecase.AssinaturaUseCase;
import com.gumeinteligenciacomercial.orcaja.application.usecase.dto.AssinaturaDto;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("assinaturas")
@RequiredArgsConstructor
public class AssinaturaController {

    private final AssinaturaUseCase assinaturaUseCase;

    @PostMapping
    public ResponseEntity<ResponseDto<AssinaturaDto>> criar(@RequestBody AssinaturaDto novaAssinatura) {
        assinaturaUseCase.criarAssinatura(novaAssinatura);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{idUsuario}")
    public ResponseEntity<Void> cancelar(@PathVariable("idUsuario") String idUsuario) {
        assinaturaUseCase.cancelar(idUsuario);
        return ResponseEntity.noContent().build();
    }
}
