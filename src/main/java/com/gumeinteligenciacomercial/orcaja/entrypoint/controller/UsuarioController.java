package com.gumeinteligenciacomercial.orcaja.entrypoint.controller;

import com.gumeinteligenciacomercial.orcaja.application.usecase.UsuarioUseCase;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.ResponseDto;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.UsuarioDto;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.VerificaoEmailDto;
import com.gumeinteligenciacomercial.orcaja.entrypoint.mapper.UsuarioMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioUseCase useCase;

    @PostMapping("/cadastro")
    public ResponseEntity<ResponseDto<UsuarioDto>> cadastrar(@RequestBody UsuarioDto novoUsuario) {
        UsuarioDto resultado = UsuarioMapper.paraDto(useCase.cadastrar(UsuarioMapper.paraDomain(novoUsuario)));
        ResponseDto<UsuarioDto> response = new ResponseDto<>(resultado);
        return ResponseEntity.created(
                UriComponentsBuilder
                        .newInstance()
                        .path("/usuarios/{id}")
                        .buildAndExpand(resultado.getId())
                        .toUri())
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDto<UsuarioDto>> consultarPorId(@PathVariable("id") String idUsuario) {
        UsuarioDto resultado = UsuarioMapper.paraDto(useCase.consultarPorId(idUsuario));
        ResponseDto<UsuarioDto> response = new ResponseDto<>(resultado);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reenvio/codigo")
    public ResponseEntity<Void> reenviarCodigo(@RequestBody UsuarioDto usuarioDto) {
        useCase.reenviarCodigoEmail(usuarioDto.getEmail());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable("id") String idUsuario) {
        useCase.deletar(idUsuario);
        return ResponseEntity.noContent().build();
    }
}
