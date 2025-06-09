package com.gumeinteligenciacomercial.orcaja.domain;

import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.UsuarioDto;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class Orcamento {
    private UUID id;
    private String conteudo;
    private LocalDate dataCriacao;
    private String titulo;
    private Usuario usuario;
}
