package com.gumeinteligenciacomercial.orcaja.entrypoint.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class OrcamentoDto {
    private UUID id;
    private String conteudo;
    private LocalDate dataCriacao;
    private String titulo;
    private UsuarioDto usuarioDto;
}
