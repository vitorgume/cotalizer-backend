package com.gumeinteligenciacomercial.orcaja.entrypoint;

import com.gumeinteligenciacomercial.orcaja.application.usecase.OrcamentoUseCase;
import com.gumeinteligenciacomercial.orcaja.domain.Orcamento;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.OrcamentoDto;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.ResponseDto;
import com.gumeinteligenciacomercial.orcaja.entrypoint.mapper.OrcamentoMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

@RestController
@RequestMapping("orcamentos")
@RequiredArgsConstructor
public class OrcamentoController {

    private final OrcamentoUseCase useCase;

    @PostMapping
    public ResponseEntity<ResponseDto<OrcamentoDto>> cadastrar(@RequestBody OrcamentoDto novoOrcamento) {
        OrcamentoDto resultado = OrcamentoMapper.paraDto(useCase.cadastrar(OrcamentoMapper.paraDomain(novoOrcamento)));
        ResponseDto<OrcamentoDto> response = new ResponseDto<>(resultado);
        return ResponseEntity.created(
                UriComponentsBuilder
                        .newInstance()
                        .path("/orcamentos/{id}")
                        .buildAndExpand(resultado.getId())
                        .toUri())
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDto<OrcamentoDto>> consultarPorId(@PathVariable("id") UUID idOrcamento) {
        OrcamentoDto resultado = OrcamentoMapper.paraDto(useCase.consultarPorId(idOrcamento));
        ResponseDto<OrcamentoDto> response = new ResponseDto<>(resultado);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/usuario/{id}")
    public ResponseEntity<ResponseDto<Page<OrcamentoDto>>> listarPorUsuario(@PathVariable("id") UUID idUsuario, @PageableDefault Pageable pageable) {
        Page<OrcamentoDto> resultado = OrcamentoMapper.paraDtos(useCase.listarPorUsuario(idUsuario, pageable));
        ResponseDto<Page<OrcamentoDto>> response = new ResponseDto<>(resultado);
        return ResponseEntity.ok(response);
    }


}
