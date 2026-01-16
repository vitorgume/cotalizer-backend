package com.gumeinteligenciacomercial.orcaja.entrypoint.controller;

import com.gumeinteligenciacomercial.orcaja.application.usecase.UsuarioUseCase;
import com.gumeinteligenciacomercial.orcaja.domain.Usuario;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.AlteracaoSenhaDto;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.ResponseDto;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.UsuarioDto;
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
                        .path("/usuarios/cadastro/{id}")
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

    @PutMapping("/inativar/{id}")
    public ResponseEntity<ResponseDto<UsuarioDto>> inativar(@PathVariable("id") String idUsuario) {
        UsuarioDto resultado = UsuarioMapper.paraDto(useCase.inativar(idUsuario));
        ResponseDto<UsuarioDto> response = new ResponseDto<>(resultado);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDto<UsuarioDto>> alterar(@PathVariable("id") String id, @RequestBody UsuarioDto novosDados) {
        UsuarioDto resultado = UsuarioMapper.paraDto(useCase.alterar(id, UsuarioMapper.paraDomain(novosDados)));
        ResponseDto<UsuarioDto> response = new ResponseDto<>(resultado);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/alterar/senha")
    public ResponseEntity<ResponseDto<Usuario>> alterarSenha(@RequestBody AlteracaoSenhaDto alteracaoSenhaDto) {
        Usuario resultado = useCase.alterarSenha(alteracaoSenhaDto.getNovaSenha(), alteracaoSenhaDto.getCodigo());
        ResponseDto<Usuario> response = new ResponseDto<>(resultado);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/somar/orcamentos/{telefone}")
    public ResponseEntity<ResponseDto<Usuario>> somarQuantidadeOrcamentos(@PathVariable("telefone") String telefone) {
        Usuario resultado = useCase.somarQuantidadeOrcamentos(telefone);
        ResponseDto<Usuario> response = new ResponseDto<>(resultado);
        return ResponseEntity.ok(response);
    }
}
